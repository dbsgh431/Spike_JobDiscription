package Spike.JobDiscription.controller;

import Spike.JobDiscription.dto.JobDto;
import Spike.JobDiscription.dto.UserDto;
import Spike.JobDiscription.entity.Job;
import Spike.JobDiscription.entity.User;
import Spike.JobDiscription.service.JobService;
import Spike.JobDiscription.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    @ModelAttribute
    public void displayUsername(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {
        model.addAttribute("username", loginUser.getEmail());
    }

    @GetMapping()
    public String jobs(Model model, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        List<Job> jobs = jobService.showAll(loginUser);
        model.addAttribute("jobs", jobs);
        model.addAttribute("user", loginUser);
        return "JDList";
    }

    @GetMapping("/add")
    public String addJobForm(Job job, Model model) {
        model.addAttribute("jobDto", new JobDto());
        return "addJob";
    }

    
    @PostMapping("/add")
    public String addJob(@ModelAttribute JobDto dto, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        jobService.create(dto, loginUser);

        return "redirect:/jobs";
    }

    @GetMapping("/edit/{id}")
    public String editJob(@PathVariable("id") Long id, Model model) {
        Job job = jobService.showJob(id);

        if (job != null) {
            DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory("yyyy-MM-dd");
            String formattedPeriod = formatterFactory.createDateTimeFormatter().format(job.getPeriod());

            model.addAttribute("job", job);
            model.addAttribute("period", formattedPeriod);
            return "editJob";
        }
        return "redirect:/jobs";
    }

    @GetMapping("/update/{id}")
    public String updateJobForm(@PathVariable("id") Long id, Model model) {
        Job job = jobService.showJob(id);
        if (job != null) {
            DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory("yyyy-MM-dd");
            String formattedPeriod = formatterFactory.createDateTimeFormatter().format(job.getPeriod());

            model.addAttribute("jobDto", job);
            model.addAttribute("period", formattedPeriod);
            return "updateJob";
        }
        return "redirect:/jobs";
    }

    @PostMapping("/update")
    public String updateJob(JobDto dto, Model model, UserDto userDto) {
        Job job = jobService.patch(dto, userDto);

        if (job != null) {
            model.addAttribute("job", job);
            return "redirect:/jobs/edit/" + job.getId();
        }
        return "redirect:/jobs";
    }

    @GetMapping("/delete/{id}")
    public String deleteJob(@PathVariable("id") Long id) {

        if (jobService.delete(id)) {
            return "redirect:/jobs";
        }
        return "editJob";

    }
}
