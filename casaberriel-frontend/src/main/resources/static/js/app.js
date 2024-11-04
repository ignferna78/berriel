//*Metodo para confirmar eliminar una reserva

newFunction();


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

$('#comprobar-disponibilidad-formBtn').on('click', function(event) {
	event.preventDefault();

	var fechaEntrada = $('#fechaEntrada').val();
	var fechaSalida = $('#fechaSalida').val();

	//console.log('Fecha de entrada enviada:', fechaEntrada);
	//console.log('Fecha de salida enviada:', fechaSalida);

	var formData = {
		fechaEntrada: fechaEntrada,
		fechaSalida: fechaSalida
	};

	$.ajax({
		url: $('#comprobar-disponibilidad-form').attr('action'),
		type: 'GET',
		data: formData,
		success: function(response) {
			$('#resultados-disponibilidad').html($(response).find('#resultados-disponibilidad').html());
		},
		error: function(error) {
			console.log("Error en la comprobación de disponibilidad: ", error);
		}
	});
});


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
	          startDate: 'today', // No permite seleccionar fechas pasadas
	          autoclose: true,
	          todayHighlight: true,
	          format: 'dd/mm/yyyy'
	      }).on('changeDate', function (e) {
	          // Obtener la fecha de entrada y establecerla como fecha mínima en el datepicker de salida
	          let fechaEntrada = $('#fechaEntrada').datepicker('getDate');
	          $('#fechaSalida').datepicker('setStartDate', fechaEntrada);
	          $('#fechaSalida').datepicker('setDate', null); // Limpiar la fecha de salida al cambiar la fecha de entrada
	      });

	      // Configurar el datepicker para la fecha de salida
	      $('#fechaSalida').datepicker({
	          startDate: 'today', // No permite seleccionar fechas pasadas
	          autoclose: true,
	          todayHighlight: true,
	          format: 'dd/mm/yyyy'
	      });

	      // Validación antes de enviar el formulario
	      $('#comprobar-disponibilidad-form').on('submit', function (e) {
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
//console.log(isUserLoggedIn);
    if (isUserLoggedIn) {
		//console.log("dentr if " + isUserLoggedIn);
        // Redirige al formulario de reservas si el usuario está logueado
        const fechaEntrada = document.getElementById('fechaEntrada').value;
        const fechaSalida = document.getElementById('fechaSalida').value;
        window.location.href = `/reservas/formReserva?fechaEntrada=${encodeURIComponent(fechaEntrada)}&fechaSalida=${encodeURIComponent(fechaSalida)}`;
		console.log('Fecha de entrada enviada:', fechaEntrada);
		console.log('Fecha de salida enviada:', fechaSalida);
    } else {
        // Muestra el modal de login si el usuario no está logueado
        $('#modalLogin').modal('show');
    }
}



document.addEventListener("DOMContentLoaded", function () {
    // Seleccionar el formulario y el botón de envío
    const contactForm = document.getElementById("contactForm");
    const contactFormBtn = document.getElementById("contactFormBtn");

    // Agregar un evento de escucha para el envío del formulario
    contactForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Evitar el envío predeterminado del formulario

        // Obtener valores de los campos del formulario
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const message = document.getElementById("message").value;

        // Validar que los campos no estén vacíos
        if (!name || !email || !message) {
            alert("Por favor, complete todos los campos.");
            return;
        }

        // Deshabilitar el botón de enviar para evitar múltiples envíos
        contactFormBtn.disabled = true;
        contactFormBtn.textContent = "Enviando...";

        // Crear un objeto con los datos del formulario
        const data = {
            name: name,
            email: email,
            message: message
        };
console.log(data);
        // Realizar la solicitud AJAX
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
		    alert("Mensaje enviado con éxito.");
		    contactForm.reset();
		})
		.catch(error => {
		    alert("Hubo un error al enviar el mensaje. Inténtelo de nuevo.");
		    console.error("Error:", error);
		})
		.finally(() => {
		    contactFormBtn.disabled = false;
		    contactFormBtn.textContent = "Enviar";
		});

    });
});








