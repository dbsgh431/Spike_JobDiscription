package Spike.JobDiscription.controller;

import Spike.JobDiscription.dto.JobDto;
import Spike.JobDiscription.dto.UserDto;
import Spike.JobDiscription.entity.Job;
import Spike.JobDiscription.entity.User;
import Spike.JobDiscription.service.JobService;
import Spike.JobDiscription.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    // 상단 네비게이션 바에 로그인한 유저 데이터 처리를 위한 모델
    @ModelAttribute
    public void usernameToNavbar(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {
        model.addAttribute("username", loginUser.getEmail());
    }

    @GetMapping()
    public String jobs(Model model, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        List<Job> jobs = jobService.showAll(loginUser);
        model.addAttribute("jobs", jobs);
        model.addAttribute("user", loginUser);
        return "jobs/JDList";
    }

    @GetMapping("/add")
    public String jobForm(Job job, Model model) {
        model.addAttribute("jobDto", new JobDto());
        return "jobs/addJob";
    }


    @PostMapping("/add")
    public String addJob(@ModelAttribute JobDto dto, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        jobService.create(dto, loginUser);
        return "redirect:/jobs";
    }

    @GetMapping("/edit/{id}")
    public String editJobForm(@PathVariable("id") Long id, Model model, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {

        if (jobService.isCorrectUser(id, loginUser)) {
            Job job = jobService.showJob(id);
            if (job != null) {
                model.addAttribute("job", job);
                return "jobs/editJob";
            }
        }
        return "redirect:/jobs";
    }

    @GetMapping("/update/{id}")
    public String updateJobForm(@PathVariable("id") Long id, Model model, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        if (jobService.isCorrectUser(id, loginUser)) {
            Job job = jobService.showJob(id);
            if (job != null) {
                model.addAttribute("job", job);
                return "jobs/updateJob";
            }
        }
        return "redirect:/jobs";
    }

    @PostMapping("/update")
    public String updateJob(JobDto dto, Model model, UserDto userDto, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        if (jobService.isCorrectUser(dto.getId(), loginUser)) {
            Job job = jobService.patch(dto, userDto);

            if (job != null) {
                model.addAttribute("job", job);
                return "redirect:/jobs/edit/" + job.getId();
            }
        }
        return "redirect:/jobs";
    }

    @PostMapping("/delete")
    public String deleteJob(Job job, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        if (jobService.isCorrectUser(job.getId(), loginUser)) {
            if (jobService.delete(job.getId())) {
                return "redirect:/jobs";
            }
        }
        return "jobs/editJob";

    }
}
