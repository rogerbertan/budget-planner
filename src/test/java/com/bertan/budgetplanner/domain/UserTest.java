package com.bertan.budgetplanner.domain;

import com.bertan.budgetplanner.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldCreateUserWithConstructorArgs() {
        User user = new User("Roger Bertan", "roger@example.com", "encoded-password");

        assertThat(user.getName()).isEqualTo("Roger Bertan");
        assertThat(user.getUsername()).isEqualTo("roger@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getId()).isNull();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldExposeUserDetailsContractAsAlwaysValid() {
        User user = new User("Roger Bertan", "roger@example.com", "encoded-password");

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void shouldReflectEnabledFlagOnIsEnabled() {
        User user = new User("Roger Bertan", "roger@example.com", "encoded-password");

        user.setEnabled(false);

        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void shouldAllowUpdatingMutableFields() {
        User user = new User("Roger Bertan", "roger@example.com", "encoded-password");

        user.setName("Roger B.");
        user.setEmail("roger.b@example.com");
        user.setPassword("new-encoded-password");

        assertThat(user.getName()).isEqualTo("Roger B.");
        assertThat(user.getUsername()).isEqualTo("roger.b@example.com");
        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    void shouldAllowSettingIdAndCreatedAtViaReflection() {
        User user = new User("Roger Bertan", "roger@example.com", "encoded-password");

        ReflectionTestUtils.setField(user, "id", 1L);

        assertThat(user.getId()).isEqualTo(1L);
    }
}