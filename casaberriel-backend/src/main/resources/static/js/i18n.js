document.addEventListener("DOMContentLoaded", function() {
    const translations = {
        en: null,
        es: null
    };

    function loadTranslations(lang) {
        return fetch(`locales/${lang}.json`)
            .then(response => response.json())
            .then(data => {
                translations[lang] = data;
                applyTranslations(lang);
            });
    }

    function applyTranslations(lang) {
        const elements = document.querySelectorAll("[data-i18n]");
        elements.forEach(el => {
            const key = el.getAttribute("data-i18n");
            const text = key.split('.').reduce((obj, key) => obj[key], translations[lang]);
            el.innerHTML = text;
        });
    }

    document.getElementById("languageSelector").addEventListener("change", (event) => {
        const selectedLang = event.target.value;
        localStorage.setItem('selectedLanguage', selectedLang);
        loadTranslations(selectedLang);
    });

    // Load selected language or default to Spanish
    const savedLang = localStorage.getItem('selectedLanguage') || 'es';
    loadTranslations(savedLang);

    // Add event listener to the button
    document.getElementById("comprobar-disponibilidad-formBtn").addEventListener("click", function(event) {
        event.preventDefault();

        const selectedLang = document.getElementById("languageSelector").value;
        localStorage.setItem('selectedLanguage', selectedLang);

        loadTranslations(selectedLang).then(() => {
            const fechaEntrada = $('#fechaEntrada').val();
            const fechaSalida = $('#fechaSalida').val();

            console.log('Fecha de entrada enviada:', fechaEntrada);
            console.log('Fecha de salida enviada:', fechaSalida);

            const formData = {
                fechaEntrada: fechaEntrada,
                fechaSalida: fechaSalida,
            };

            $.ajax({
                url: $('#comprobar-disponibilidad-form').attr('action'),
                type: 'GET',
                data: formData,
                success: function(response) {
                    $('#resultados-disponibilidad').html($(response).find('#resultados-disponibilidad').html());
                    applyTranslations(selectedLang); // Apply translations to the new content
                },
                error: function(error) {
                    console.log("Error en la comprobación de disponibilidad: ", error);
                }
            });
        });
    });
	document.addEventListener("DOMContentLoaded", function() {
	    const languageSelector = document.getElementById("languageSelector");

	    function updateFlag() {
	        const selectedOption = languageSelector.options[languageSelector.selectedIndex];
	        const flagUrl = selectedOption.getAttribute("data-icon");
	        languageSelector.style.backgroundImage = `url(${flagUrl})`;
	    }

	    // Initial flag update
	    updateFlag();

	    // Update flag on change
	    languageSelector.addEventListener("change", updateFlag);
	});

});



