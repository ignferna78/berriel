package com.project.casaberriel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

import com.project.casaberriel.controllers.RegistroUsuarioController;
import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.service.IEmailService;
import com.project.casaberriel.service.UsuarioService;
import com.project.casaberriel.utils.LoginRequest;

public class RegistroUsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;
    
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Model model;
    
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    
    @Mock
    private IEmailService emailService;

    @InjectMocks
    private RegistroUsuarioController registroUsuarioController;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setUp() {
    	 MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidarCredenciales_ValidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword(passwordEncoder.encode("password"));

        when(usuarioService.findUserByEmail("test@example.com")).thenReturn(usuario);

        // Act
        ResponseEntity<?> response = registroUsuarioController.validarCredenciales(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Credenciales válidas", ((Map<String, String>) response.getBody()).get("message"));
    }

    @Test
    public void testValidarCredenciales_InvalidPassword() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword(passwordEncoder.encode("password"));

        when(usuarioService.findUserByEmail("test@example.com")).thenReturn(usuario);

        // Act
        ResponseEntity<?> response = registroUsuarioController.validarCredenciales(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Contraseña incorrecta", ((Map<String, String>) response.getBody()).get("message"));
    }

    @Test
    public void testValidarCredenciales_UserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(usuarioService.findUserByEmail("nonexistent@example.com")).thenThrow(new UsernameNotFoundException("El email no existe"));

        // Act
        ResponseEntity<?> response = registroUsuarioController.validarCredenciales(loginRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("El email no existe", ((Map<String, String>) response.getBody()).get("message"));
    }
    
    @Test
    public void testGuardarCuentaUser_Success() throws Exception {
        // Arrange
        UsuarioRegistroDto registroDto = new UsuarioRegistroDto();
        registroDto.setEmail("test@example.com");
        registroDto.setPassword("password");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        when(usuarioService.guardar(registroDto)).thenReturn(usuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        // Act
        String viewName = registroUsuarioController.guardarCuentaUser(registroDto, model, false, false);

        // Assert
        assertEquals("redirect:/registro/inicio?exito", viewName);
        verify(emailService).sendUsuarioConfirmation(usuario, false, false);
    }

    @Test
    public void testGuardarCuentaUser_EmailAlreadyExists() throws Exception {
        // Arrange
        UsuarioRegistroDto registroDto = new UsuarioRegistroDto();
        registroDto.setEmail("test@example.com");
        registroDto.setPassword("password");

        when(usuarioService.guardar(registroDto)).thenThrow(new DataIntegrityViolationException(""));

        // Act
        String viewName = registroUsuarioController.guardarCuentaUser(registroDto, model, false, false);

        // Assert
        assertEquals("registro", viewName);
        verify(model).addAttribute("error", "El email ya está registrado. Por favor, usa otro email.");
    }

    @Test
    public void testGuardarCuentaUser_IllegalArgumentException() throws Exception {
        // Arrange
        UsuarioRegistroDto registroDto = new UsuarioRegistroDto();
        registroDto.setEmail("test@example.com");
        registroDto.setPassword("password");

        when(usuarioService.guardar(registroDto)).thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        String viewName = registroUsuarioController.guardarCuentaUser(registroDto, model, false, false);

        // Assert
        assertEquals("registro", viewName);
        verify(model).addAttribute("error", "Invalid data");
    }
    
    @Test
    public void testVerPaginaDeInicio() {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(new ArrayList<>());

        // Act
        String viewName = registroUsuarioController.verPaginaDeInicio(model);

        // Assert
        assertEquals("registro", viewName);
        verify(model).addAttribute("usuarios", new ArrayList<>());
    }

    @Test
    public void testVerDatosUsuario() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("test@example.com");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        when(usuarioService.findUserByEmail("test@example.com")).thenReturn(usuario);

        // Act
        String viewName = registroUsuarioController.verDatosUsuario(model);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(model).addAttribute("usuario", usuario);
    }

    @Test
    public void testVerDatosUsuario_Exception() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("test@example.com");

        when(usuarioService.findUserByEmail("test@example.com")).thenThrow(new RuntimeException("User not found"));

        // Act
        String viewName = registroUsuarioController.verDatosUsuario(model);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(model).addAttribute("usuario", null);
    }
    
    @Test
    public void testMostrarFormularioEdicion() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioService.findUserById(1L)).thenReturn(usuario);

        // Act
        String viewName = registroUsuarioController.mostrarFormularioEdicion(1L, model);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(model).addAttribute("usuario", usuario);
    }

    @Test
    public void testEditarUsuario_Success() throws MessagingException {
        // Arrange
        UsuarioRegistroDto usuarioActualizado = new UsuarioRegistroDto();
        usuarioActualizado.setNombre("Nuevo Nombre");
        usuarioActualizado.setApellidos("Nuevos Apellidos");
        usuarioActualizado.setDireccion("Nueva Dirección");
        usuarioActualizado.setTelefono("123456789");
        usuarioActualizado.setEmail("nuevo@example.com");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);

        when(usuarioService.findUserById(1L)).thenReturn(usuarioExistente);

        // Act
        String viewName = registroUsuarioController.editarUsuario(1L, usuarioActualizado, model, true);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(usuarioService).updateUser(usuarioExistente);
        verify(emailService).sendUsuarioConfirmation(usuarioExistente, false, true);
        verify(model).addAttribute("message", "Usuario modificado con éxito.");
    }

    @Test
    public void testEditarUsuario_Exception() {
        // Arrange
        UsuarioRegistroDto usuarioActualizado = new UsuarioRegistroDto();
        usuarioActualizado.setNombre("Nuevo Nombre");

        when(usuarioService.findUserById(1L)).thenThrow(new RuntimeException("Error"));

        // Act
        String viewName = registroUsuarioController.editarUsuario(1L, usuarioActualizado, model, true);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(model).addAttribute("error", "Ocurrió un error al actualizar el usuario.");
    }





    @Test
    public void testEliminarUsuario_DataIntegrityViolationException() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioService.findUserById(1L)).thenReturn(usuario);
        doThrow(new DataIntegrityViolationException("")).when(usuarioService).deleteUserById(1L);

        // Act
        String viewName = registroUsuarioController.eliminarUsuario(1L, model, request, response, true);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(model).addAttribute("error", "No se puede eliminar el usuario debido a una violación de integridad de datos.");
    }

    @Test
    public void testEliminarUsuario_Exception() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioService.findUserById(1L)).thenReturn(usuario);
        doThrow(new RuntimeException("Error")).when(usuarioService).deleteUserById(1L);

        // Act
        String viewName = registroUsuarioController.eliminarUsuario(1L, model, request, response, true);

        // Assert
        assertEquals("detalle_usuario", viewName);
        verify(model).addAttribute("error", "Ocurrió un error al eliminar el usuario.");
    }
}
