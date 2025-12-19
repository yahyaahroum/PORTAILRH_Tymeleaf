var contextPath = '';

fetch(window.location.pathname.split('/')[1] === 'SC' ? '/SC/api/config/context-path' : '/SC-DEV/api/config/context-path')
    .then(response => response.json())
    .then(data => {
        contextPath = data.contextPath;
        console.log("Contexte réel:", contextPath);
        // ex: fetch(`${contextPath}/BudgetAffaire/11797`)
    })
    .catch(() => {
        contextPath = ''; // fallback si API inaccessible
    });