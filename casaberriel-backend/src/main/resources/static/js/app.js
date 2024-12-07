//*Metodo para confirmar eliminar una reserva

newFunction();


function actualizarTraducciones() {
	// Asegúrate de que i18next o tu biblioteca de traducción se ejecute aquí
	i18next.init({
		// Configuración de i18next
	}, function(err, t) {
		// Actualizar los textos con la función de traducción
		document.querySelectorAll('[data-i18n]').forEach(function(el) {
			el.innerHTML = i18next.t(el.getAttribute('data-i18n'));
		});
	});
}


function newFunction() {
	$('#deleteModal').on('show.bs.modal', function(event) {
		let button = $(event.relatedTarget); // Botón que activó el modal
		let id = button.data('id'); // Extrae la información de data-id

		let modal = $(this);
		let deleteUrl = '/reservas/eliminar/' + id;
		modal.find('#confirmDeleteButton').attr('href', deleteUrl);
	});
}

function limpiarCriterios() {
	const form = document.getElementById("formReserva");
	const inputs = form.querySelectorAll("input, select");
	inputs.forEach(input => {
		if (input.type === "checkbox" || input.type === "radio") {
			input.checked = false;
		} else {
			input.value = "";
		}
	});
}




// Mostrar el botón cuando el usuario hace scroll hacia abajo 20px
window.onscroll = function() {
	mostrarBoton();
};

function mostrarBoton() {
	const btn = document.getElementById("btnSubir");
	if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
		btn.style.display = "block"; // Mostrar el botón
	} else {
		btn.style.display = "none"; // Ocultar el botón
	}
}

// Volver al inicio de la página
function subirArriba() {
	document.body.scrollTop = 0; // Para Safari
	document.documentElement.scrollTop = 0; // Para Chrome, Firefox, IE y Opera
}

$(function() {
	$.fn.datepicker.dates['es'] = {
		days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
		daysShort: ['Dom', 'Lun', 'Mar', 'Mié', 'Juv', 'Vie', 'Sáb'],
		daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sá'],
		months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
		monthsShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
		today: 'Hoy',
		clear: 'Limpiar',
		format: 'dd/mm/yyyy',
		titleFormat: "MM yyyy",
		weekStart: 1
	};

	$(".datepicker").datepicker({
		startDate: 'today',
		language: 'es',
		autoclose: true,
		todayHighlight: true,
		format: 'dd/mm/yyyy',
	}).datepicker('update', new Date());

	$('#fechaEntrada').datepicker({
	    startDate: new Date(), // No permite seleccionar fechas pasadas
	    autoclose: true,
	    todayHighlight: true,
	    format: 'dd/mm/yyyy'
	}).on('changeDate', function(e) {
	    // Obtener la fecha de entrada y establecerla como fecha mínima en el datepicker de salida
	    let fechaEntrada = new Date(e.date.valueOf());
	    
	    // Establecer la fecha mínima de salida a un día después de la fecha de entrada
	    fechaEntrada.setDate(fechaEntrada.getDate() + 1); // Asegura que la fecha de salida sea al menos 1 día después

	    // Configurar el datepicker de salida para que no permita seleccionar una fecha antes de la fecha mínima
	    $('#fechaSalida').datepicker('setStartDate', fechaEntrada);
	    $('#fechaSalida').datepicker('update'); // Limpiar la fecha de salida al cambiar la fecha de entrada

	    // Validar que la fecha de salida actual no sea anterior a la fecha de entrada
	    let fechaSalida = new Date($('#fechaSalida').datepicker('getDate'));
	    if (fechaSalida && fechaSalida < fechaEntrada) {
	        $('#fechaSalida').datepicker('setDate', fechaEntrada); // Si es anterior, ajustamos la fecha de salida
	    }
	});

	// Configurar el datepicker para la fecha de salida
	$('#fechaSalida').datepicker({
	    startDate: '+1d', // No permite seleccionar el mismo día de hoy como fecha de salida
	    autoclose: true,
	    todayHighlight: true,
	    format: 'dd/mm/yyyy'
	}).on('changeDate', function(e) {
	    // Validar que la fecha de salida no sea anterior a la fecha de entrada
	    let fechaEntrada = new Date($('#fechaEntrada').datepicker('getDate'));
	    let fechaSalida = new Date(e.date.valueOf());

	    if (fechaSalida < fechaEntrada) {
	        // Si la fecha de salida es anterior a la fecha de entrada, la corregimos
	        $('#fechaSalida').datepicker('setDate', fechaEntrada);
	        alert('La fecha de salida no puede ser anterior a la fecha de entrada.');
	    }
	});

	// Validación antes de enviar el formulario
	$('#comprobar-disponibilidad-form').on('submit', function(e) {
		let fechaEntrada = $('#fechaEntrada').datepicker('getDate');
		let fechaSalida = $('#fechaSalida').datepicker('getDate');

		// Comprobar si la fecha de salida es anterior a la fecha de entrada
		if (fechaSalida && fechaEntrada && fechaSalida <= fechaEntrada) {
			e.preventDefault(); // Evitar que se envíe el formulario
			alert('La fecha de salida debe ser posterior a la fecha de entrada.');
			return false;
		}

		// Comprobar que ambas fechas han sido seleccionadas
		if (!fechaEntrada || !fechaSalida) {
			e.preventDefault();
			alert('Por favor, selecciona ambas fechas.');
			return false;
		}
	});


});




function formatDate(timestamp) {
	if (timestamp) {
		var date = new Date(timestamp);
		var day = ('0' + date.getDate()).slice(-2);
		var month = ('0' + (date.getMonth() + 1)).slice(-2);
		var year = date.getFullYear();
		return day + '/' + month + '/' + year;
	}
	return '';
}


//Modal de logout
document.addEventListener("DOMContentLoaded", function() {
	// Manejar el clic en el botón de logout en la barra de navegación
	const element = document.querySelector("a[href='/logout']");
	if (element) {
		element.addEventListener("click", function(event) {
			event.preventDefault(); // Prevenir el logout inmediato
			$('#logoutConfirmModal').modal('show'); // Mostrar el modal de confirmación
		});
	}

	// Ejecutar el logout si se confirma
	document.getElementById("confirmLogoutBtn").addEventListener("click", function() {
		window.location.href = "/logout?logout=true"; // Redirigir al endpoint de logout
	});

});


function handleReservar() {
	// Obtén el valor de isUserLoggedIn desde el input oculto
	const isUserLoggedIn = document.getElementById('isUserLoggedIn').value === 'true';
	const fechaEntrada = document.getElementById('fechaEntrada').value;
	const fechaSalida = document.getElementById('fechaSalida').value;
	if (isUserLoggedIn) {
		// Redirige al formulario de reservas si el usuario está logueado
		window.location.href = `/reservas/formReserva?fechaEntrada=${fechaEntrada}&fechaSalida=${fechaSalida}`;
		console.log('Fecha de entrada enviada:', fechaEntrada);
		console.log('Fecha de salida enviada:', fechaSalida);
	} else {
		// Muestra el modal de login si el usuario no está logueado
		$('#loginModal').modal('show');
	}
}

document.addEventListener("DOMContentLoaded", function() {
	// Seleccionar el formulario y el botón de envío
	const contactForm = document.getElementById("contactForm");
	const contactFormBtn = document.getElementById("contactFormBtn");

	// Función para validar el formato del email
	function isValidEmail(email) {
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Expresión regular para validar email
		return emailRegex.test(email);
	}

	// Agregar un evento de escucha para el envío del formulario
	contactForm.addEventListener("submit", function(event) {
		event.preventDefault(); // Evitar el envío predeterminado del formulario
		const email = document.querySelector("#mail").value.trim();
		// Obtener valores de los campos del formulario
		const name = document.getElementById("name").value.trim();
		const message = document.getElementById("messages").value.trim();

		// Validar que los campos no estén vacíos
		if (!name || !email || !message) {
			alert("Por favor, complete todos los campos.");
			return;
		}

		// Validar formato del email
		if (!isValidEmail(email)) {
			alert("Por favor, introduzca un email válido.");
			document.getElementById("mail").focus(); // Enfocar el campo del email

			return;
		}

		// Deshabilitar el botón de enviar para evitar múltiples envíos
		contactFormBtn.disabled = true;
		contactFormBtn.textContent = "Enviando...";
		contactForm.reset();

		// Realizar la solicitud AJAX
		const data = {
			name: name,
			email: email,
			message: message
		};

		fetch("/api/send-email", {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(data)
		})
			.then(response => {
				if (!response.ok) {
					return response.text().then(text => {
						throw new Error(`Error en el envío del mensaje: ${text}`);
					});
				}
				return response.json();
			})
			.then(result => {
				console.log("Correo enviado:", result);
				document.querySelector('.alert-success').style.display = 'block';
				contactFormBtn.disabled = false;
				contactForm.reset();
				contactFormBtn.textContent = "Enviar";

			})
			.catch(error => {
				console.error("Error:", error);
				alert("Hubo un error al enviar el mensaje. Inténtelo de nuevo.");
			});
	});
});



window.onload = function() {
	var messageDiv = document.getElementById('message');
	if (messageDiv) {
		setTimeout(function() {
			messageDiv.classList.add('hidden');
			setTimeout(function() {
				messageDiv.style.display = 'none';
			}, 1000); 
		}, 5000);
	}
};


document.addEventListener('DOMContentLoaded', () => {
	const loginForm = document.querySelector('#loginModal form');
	const errorDiv = document.getElementById('error');

	loginForm.addEventListener('submit', async (e) => {
		// Ocultar mensajes de error previos
		errorDiv.style.display = 'none';
		errorDiv.textContent = '';

		// Prevenir el envío del formulario de forma predeterminada
		e.preventDefault();

		// Obtener valores de los campos
		const email = document.getElementById('email').value.trim();
		const password = document.getElementById('password').value.trim();

		// Validar que ambos campos no estén vacíos
		if (!email || !password) {
			errorDiv.textContent = 'Por favor, completa todos los campos.';
			errorDiv.style.display = 'block';
			return;
		}

		// Validar email con una expresión regular
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		if (!emailRegex.test(email)) {
			errorDiv.textContent = 'Por favor, ingresa un email válido.';
			errorDiv.style.display = 'block';
			return;
		}

		try {
			// Verificar las credenciales
			const response = await fetch('/registro/validarCredenciales', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ email, password })
			});

			if (response.ok) {
				// Credenciales válidas, permite el envío del formulario
				loginForm.submit();
			} else if (response.status === 401) {
				// Contraseña incorrecta
				const errorData = await response.json();
				errorDiv.textContent = errorData.message;
				errorDiv.style.display = 'block';
			} else if (response.status === 404) {
				// Usuario no encontrado
				const errorData = await response.json();
				errorDiv.textContent = errorData.message;
				errorDiv.style.display = 'block';
			} else {
				// Error inesperado
				errorDiv.textContent = 'Error inesperado al verificar las credenciales.';
				errorDiv.style.display = 'block';
			}
		} catch (error) {
			// Manejar errores de red
			errorDiv.textContent = 'Error al conectarse con el servidor.';
			errorDiv.style.display = 'block';
			console.error('Error en la verificación de credenciales:', error);
		}
	});
});



document.addEventListener('DOMContentLoaded', function() {
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	tooltipTriggerList.forEach(function(tooltipTriggerEl) {
		new bootstrap.Tooltip(tooltipTriggerEl);
	});

	const passwordField = document.getElementById('password');
	const form = passwordField.closest('form'); 
	const errorDiv = document.getElementById('password-error');

	form.addEventListener('submit', function(event) {
		const password = passwordField.value;

		// Limpiar mensajes previos
		errorDiv.style.display = 'none';
		errorDiv.textContent = '';

		// Validaciones
		const minLength = 5;
		const specialCharRegex = /[^a-zA-Z0-9]/; // Detecta caracteres especiales

		if (password.length < minLength) {
			errorDiv.textContent = 'La contraseña debe tener al menos 5 caracteres.';
			errorDiv.style.display = 'block';
			event.preventDefault(); // Evita el envío del formulario
			return;
		}

		if (specialCharRegex.test(password)) {
			errorDiv.textContent = 'La contraseña no debe contener caracteres especiales.';
			errorDiv.style.display = 'block';
			event.preventDefault(); // Evita el envío del formulario
			return;
		}
	});
});







