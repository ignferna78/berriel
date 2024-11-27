package com.project.casaberriel;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.casaberriel.dto.UsuarioRegistroDto;
import com.project.casaberriel.model.usuarios.Rol;
import com.project.casaberriel.model.usuarios.Usuario;
import com.project.casaberriel.repositorios.UsuarioRepositorio;
import com.project.casaberriel.service.Impl.UsuarioServiceImpl;

public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private Usuario usuario;
    
    @Mock
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
    
    @Test
    public void testUpdatePassword() {
        // Configurar datos de prueba
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPassword("oldPassword");
        usuario.setPasswordResetToken("resetToken");
        usuario.setTokenExpirationTime(2L);

        String nuevaPassword = "newPassword";
        String passwordCodificada = "encodedNewPassword";

        // Configurar comportamiento del mock del codificador
        Mockito.when(passwordEncoder.encode(nuevaPassword)).thenReturn(passwordCodificada);

        // Llamar al método
        usuarioService.updatePassword(usuario, nuevaPassword);

        // Verificar que se codificó la nueva contraseña
        Mockito.verify(passwordEncoder).encode(nuevaPassword);

        // Verificar que se actualizó la contraseña del usuario
        Assert.assertEquals("La contraseña debe estar codificada", passwordCodificada, usuario.getPassword());

        // Verificar que el token y el tiempo de expiración se limpiaron
        Assert.assertNull("El token debe ser nulo después de actualizar la contraseña", usuario.getPasswordResetToken());
        Assert.assertNull("El tiempo de expiración debe ser nulo después de actualizar la contraseña", usuario.getTokenExpirationTime());

        // Verificar que se guardó el usuario en el repositorio
        Mockito.verify(usuarioRepositorio).save(usuario);
    }
    
    @Test
    public void testSavePasswordResetToken() {
        // Configurar un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Simular el token
        String token = "reset-token";

        // Llamar al método bajo prueba
        usuarioService.savePasswordResetToken(usuario, token);

        // Verificar que el token se haya configurado correctamente
        assertEquals(token, usuario.getPasswordResetToken());
        assertNotNull(usuario.getTokenExpirationTime());
      

        // Verificar que el repositorio fue llamado para guardar el usuario
        verify(usuarioRepositorio).save(usuario);
    }
  
    @Test
    void testMapearAutoridadesRoles() {
        // Crear roles de prueba
        Rol rol1 = new Rol();
        rol1.setNombre("ROLE_USER");
        
        Rol rol2 = new Rol();
        rol2.setNombre("ROLE_ADMIN");

        List<Rol> roles = Arrays.asList(rol1, rol2);

        // Llamar al método
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(passwordEncoder, usuarioRepositorio); // Asume que este método está en esta clase
        Collection<? extends GrantedAuthority> autoridades = usuarioService.mapearAutoridadesRoles(roles);

        // Validar el tamaño de la colección resultante
        assertEquals(2, autoridades.size());

        // Validar los nombres de las autoridades
        assertTrue(autoridades.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(autoridades.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    

}
