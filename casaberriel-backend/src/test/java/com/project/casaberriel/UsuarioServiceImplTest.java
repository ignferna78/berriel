package com.project.casaberriel;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.Impl.UsuarioServiceImpl;

public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    
    private BCryptPasswordEncoder passwordEncoder;  

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks

        // Prepara un objeto usuario de ejemplo
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellidos("Perez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setPassword("password123");
    }

    @Test
    public void testGuardarUsuario() {
        // Preparar el DTO de registro
        UsuarioRegistroDto registroDto = new UsuarioRegistroDto();
        registroDto.setNombre("Juan");
        registroDto.setApellidos("Perez");
        registroDto.setEmail("juan.perez@example.com");
        registroDto.setPassword("password123");
        registroDto.setTelefono("123456789");

        // Simular el comportamiento del repositorio
        when(usuarioRepositorio.save(any(Usuario.class))).thenReturn(usuario);

        // Ejecutar el método
        Usuario resultado = usuarioService.guardar(registroDto);

        // Validar los resultados
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Perez", resultado.getApellidos());
        assertEquals("juan.perez@example.com", resultado.getEmail());
        assertNotNull(resultado.getPassword()); // La contraseña debe estar encriptada
    }



    @Test
    public void testFindUserByEmail() {
        // Simulamos que el usuario existe en la base de datos
        when(usuarioRepositorio.findUserByEmail("juan.perez@example.com")).thenReturn(Optional.of(usuario));

        // Llamamos al método findUserByEmail
        Usuario resultado = usuarioService.findUserByEmail("juan.perez@example.com");

        // Verificar el resultado
        assertNotNull(resultado);
        assertEquals("juan.perez@example.com", resultado.getEmail());
    }

    @Test
    public void testFindUserByEmailNoFound() {
        // Simulamos que no existe un usuario con el email dado
        when(usuarioRepositorio.findUserByEmail("juan.perez@example.com")).thenReturn(Optional.empty());

        // Llamamos al método y verificamos que lanza una excepción
        assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.findUserByEmail("juan.perez@example.com");
        });
    }

    @Test
    public void testValidarTelefono() {
        // Método de validación de teléfono con un valor válido
        assertDoesNotThrow(() -> usuarioService.validarTelefono("123456789"));

        // Método de validación de teléfono con un valor inválido
        assertThrows(IllegalArgumentException.class, () -> usuarioService.validarTelefono("123"));
        assertThrows(IllegalArgumentException.class, () -> usuarioService.validarTelefono("1234567891234"));
        assertThrows(IllegalArgumentException.class, () -> usuarioService.validarTelefono("123456789abc"));
    }
    

    @Test
    public void testListarUsuarios() {
        // Datos de prueba
        Usuario usuario1 = new Usuario("nacho","fernandez","calle","nsoid@gmail.com","123456789","123456789", null);
        Usuario usuario2 = new Usuario("pepe","fernandez","calle","nsoid@gmail.com","123456789","123456789", null);

        // Configuramos el comportamiento del mock
        when(usuarioRepositorio.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        // Llamamos al método que queremos probar
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // Verificamos que el resultado sea el esperado
        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
        assertEquals("nacho", usuarios.get(0).getNombre());
        assertEquals("pepe", usuarios.get(1).getNombre());

        // Verificamos que el repositorio haya sido llamado correctamente
        verify(usuarioRepositorio).findAll();
    }
    
    @Test
    public void testFindUserById_WhenUserExists() {
        // Datos de prueba
        Long userId = 1L;
        Usuario usuario1 = new Usuario("nacho","fernandez","calle","nsoid@gmail.com","123456789","123456789", null);

        // Configuramos el comportamiento del mock
        when(usuarioRepositorio.findById(userId)).thenReturn(Optional.of(usuario));

        // Llamamos al método que queremos probar
        Usuario result = usuarioService.findUserById(userId);

        // Verificamos que el resultado no sea null y que el usuario sea el esperado
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Juan", result.getNombre());
        assertEquals("juan.perez@example.com", result.getEmail());

        // Verificamos que el repositorio haya sido llamado con el ID correcto
        verify(usuarioRepositorio).findById(userId);
    }
    @Test
    public void testFindUserById_WhenUserDoesNotExist() {
        // Datos de prueba
        Long userId = 1L;

        // Configuramos el comportamiento del mock para devolver un Optional vacío
        when(usuarioRepositorio.findById(userId)).thenReturn(Optional.empty());

        // Llamamos al método que queremos probar
        Usuario result = usuarioService.findUserById(userId);

        // Verificamos que el resultado sea null
        assertNull(result);

        // Verificamos que el repositorio haya sido llamado con el ID correcto
        verify(usuarioRepositorio).findById(userId);
    }
    
    @Test
    public void testDeleteUserById() {
        // Datos de prueba
        Long userId = 1L;

        // Llamamos al método que queremos probar
        usuarioService.deleteUserById(userId);

        // Verificamos que el método deleteById haya sido llamado con el ID correcto
        verify(usuarioRepositorio).deleteById(userId);

        // No necesitamos un assert, ya que el método no devuelve nada. La verificación de "deleteById" es suficiente.
    }
    
    @Test
    public void testUpdateUser() {
        // Creamos un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Pérez");

        // Llamamos al método que queremos probar
        usuarioService.updateUser(usuario);

        // Verificamos que el método save haya sido llamado con el usuario correcto
        verify(usuarioRepositorio).save(usuario);

        // No necesitamos un assert, ya que el método no devuelve nada. La verificación de "save" es suficiente.
    }
    
    @Test
    public void testFindByPasswordResetToken_ValidToken() throws Exception {
        // Creamos un usuario de prueba con un token válido y un tiempo de expiración válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPasswordResetToken("valid-token");
        usuario.setTokenExpirationTime(System.currentTimeMillis() + 10000);  // Expiración en 10 segundos

        // Simulamos el comportamiento del repositorio para devolver el usuario cuando se busque por el token
        when(usuarioRepositorio.findByPasswordResetToken("valid-token")).thenReturn(Optional.of(usuario));

        // Llamamos al método
        Usuario result = usuarioService.findByPasswordResetToken("valid-token");

        // Verificamos que el resultado es el usuario correcto
        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());

        // Verificamos que el método findByPasswordResetToken fue llamado con el token correcto
        verify(usuarioRepositorio).findByPasswordResetToken("valid-token");
    }
    
    @Test
    public void testFindByPasswordResetToken_InvalidToken() {
        // Simulamos el comportamiento del repositorio para devolver un Optional vacío cuando se busque por el token
        when(usuarioRepositorio.findByPasswordResetToken("invalid-token")).thenReturn(Optional.empty());

        // Llamamos al método y verificamos que se lanza una excepción
        assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.findByPasswordResetToken("invalid-token");
        });

        // Verificamos que el método findByPasswordResetToken fue llamado con el token incorrecto
        verify(usuarioRepositorio).findByPasswordResetToken("invalid-token");
    }
    
    @Test
    public void testFindByPasswordResetToken_ExpiredToken() throws Exception {
        // Creamos un usuario con un token expirado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPasswordResetToken("expired-token");
        usuario.setTokenExpirationTime(System.currentTimeMillis() - 10000);  // Expiración en 10 segundos en el pasado

        // Simulamos el comportamiento del repositorio para devolver el usuario cuando se busque por el token
        when(usuarioRepositorio.findByPasswordResetToken("expired-token")).thenReturn(Optional.of(usuario));

        // Llamamos al método y verificamos que se lanza una excepción porque el token está caducado
        assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.findByPasswordResetToken("expired-token");
        });

        // Verificamos que el método findByPasswordResetToken fue llamado con el token caducado
        verify(usuarioRepositorio).findByPasswordResetToken("expired-token");
    }
    
  
}
