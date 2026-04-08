package com.evaluation.employee_eval.security;

import com.evaluation.employee_eval.domain.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Employee employee;

    public CustomUserDetails(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() { return this.employee; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole()));
    }

    @Override
    public String getPassword() { return employee.getPassword(); }

    @Override
    public String getUsername() { return employee.getLoginId(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
