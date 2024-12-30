// Table pour stocker les étudiants
const studentTableBody = document.getElementById("studentTableBody");
const addStudentForm = document.getElementById("addStudentForm");

let students = [];

// Fonction pour afficher la liste des étudiants
function displayStudents() {
    // Vider la table avant de réafficher les étudiants
    studentTableBody.innerHTML = "";
    
    // Ajouter chaque étudiant à la table
    students.forEach((student, index) => {
        const row = document.createElement("tr");
        
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${student.name}</td>
            <td>${student.firstName}</td>
            <td>${student.age}</td>
            <td>${student.email}</td>
        `;
        
        studentTableBody.appendChild(row);
    });
}

// Ajouter un étudiant via le formulaire
addStudentForm.addEventListener("submit", function (e) {
    e.preventDefault();
    
    const name = document.getElementById("studentName").value;
    const firstName = document.getElementById("studentFirstName").value;
    const age = document.getElementById("studentAge").value;
    const email = document.getElementById("studentEmail").value;

    // Créer un nouvel étudiant
    const newStudent = { name, firstName, age, email };

    // Ajouter l'étudiant à la liste
    students.push(newStudent);

    // Réafficher la liste des étudiants
    displayStudents();

    // Réinitialiser le formulaire
    addStudentForm.reset();
});

// Afficher les étudiants initiaux
displayStudents();