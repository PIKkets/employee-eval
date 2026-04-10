package com.evaluation.employee_eval.security;

import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.mapper.EmployeeMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final EmployeeMapper employeeMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String loginId = request.getParameter("username");
        String errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";

        if (exception instanceof BadCredentialsException) {
            if (loginId != null && !loginId.isEmpty()) {
                Employee employee = employeeMapper.findByLoginId(loginId);
                if (employee != null) {
                    employeeMapper.updateLoginFailure(loginId);
                    int failCount = (employee.getFailedAttempts() == null ? 0 : employee.getFailedAttempts()) + 1;
                    if (failCount >= 5) {
                        employeeMapper.lockAccount(loginId);
                        errorMessage = "비밀번호 5회 오류로 계정이 잠겼습니다. 관리자에게 문의하세요.";
                    } else {
                        errorMessage = "비밀번호가 올바르지 않습니다. (실패 " + failCount + "/5)";
                    }
                }
            }
        } else if (exception instanceof LockedException) {
            errorMessage = "계정이 잠겨 있습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "퇴사 처리된 계정입니다. 로그인이 제한됩니다.";
        } else {
            errorMessage = "로그인에 실패했습니다. 관리자에게 문의하세요.";
        }

        setDefaultFailureUrl("/login?error=true&exception=" + URLEncoder.encode(errorMessage, "UTF-8"));
        super.onAuthenticationFailure(request, response, exception);
    }
}
