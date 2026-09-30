package com.project.casaberriel;

import com.project.casaberriel.config.CustomAuthenticationSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationRedirectTest {
    @Test
    void adminGoesToAdministration() throws Exception {
        assertRedirect("ROLE_ADMIN", "/admin/lista");
    }

    @Test
    void regularUserGoesHome() throws Exception {
        assertRedirect("ROLE_USER", "/home/index");
    }

    private void assertRedirect(String role, String expected) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new CustomAuthenticationSuccessHandler().onAuthenticationSuccess(
            new MockHttpServletRequest(), response,
            new UsernamePasswordAuthenticationToken("test", null,
                AuthorityUtils.createAuthorityList(role)));
        assertEquals(expected, response.getRedirectedUrl());
    }
}
