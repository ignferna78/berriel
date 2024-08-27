// i18n.js

const translations = {
    en: null,
    es: null
};

function loadTranslations(lang) {
    fetch(`locales/${lang}.json`)
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
    loadTranslations(selectedLang);
});

// Load default language (Spanish)
loadTranslations('es');
