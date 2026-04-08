package com.evaluation.employee_eval.security;

import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeMapper employeeMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeMapper.findByLoginId(username);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new CustomUserDetails(employee);
    }
}
