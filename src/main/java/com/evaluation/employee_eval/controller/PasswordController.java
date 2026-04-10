package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.mapper.EmployeeMapper;
import com.evaluation.employee_eval.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PasswordController {

    private final EmployeeMapper employeeMapper;

    @GetMapping("/password")
    public String passwordForm() {
        return "auth/password";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        
        String storedPassword = userDetails.getPassword();
        // Since `{noop}` is prepended, we strip it or match it
        String rawStored = storedPassword.replace("{noop}", "");

        if (!rawStored.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/password";
        }

        employeeMapper.updatePassword(userDetails.getEmployee().getId(), "{noop}" + newPassword);
        // 사용자 객체 세션에 즉시 반영
        userDetails.getEmployee().setPassword("{noop}" + newPassword);
        
        redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
        return "redirect:/profile/password";
    }
}
