package config.java;
//fournir une interface graphique 
import javax.swing.*;//pour cree l'interface graphique
import java.awt.*;
import java.util.List;

public class ContactManagerFram extends JFrame {
    // Modèle de liste pour afficher les contacts
    private DefaultListModel<Contact> contactListModel;
    // Liste visuelle des contacts
    private JList<Contact> contactList;
    // Champs de texte pour les informations du contact et la recherche
    private JTextField nameField, prenomField, emailField, phoneField, searchField;
    // Objet d'accès aux données pour interagir avec la base de données
    private ContactDAO contactDAO;

    // Couleurs personnalisées pour l'interface utilisateur
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Bleu très pâle (AliceBlue)
    private final Color BUTTON_COLOR = new Color(70, 130, 180); // Bleu acier (SteelBlue)
    private final Color TEXT_COLOR = new Color(25, 25, 112); // Bleu nuit (MidnightBlue)
    private final Color LIST_BACKGROUND = Color.WHITE; // Fond de la liste blanc
    private final Color LIST_SELECTION = new Color(100, 149, 237); // Bleu maïs (CornflowerBlue) pour la sélection

    /**
     * Constructeur de la fenêtre principale du gestionnaire de contacts.
     * Configure la fenêtre, initialise les composants et charge les contacts.
     */
    public ContactManagerFram() {
        setTitle("Gestionnaire de Contacts"); // Titre de la fenêtre
        setSize(800, 700); // Taille de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Action par défaut à la fermeture
        setLayout(new BorderLayout(10, 10)); // Utilise un BorderLayout avec des espaces
        getContentPane().setBackground(BACKGROUND_COLOR); // Définit la couleur de fond du contenu

        contactDAO = new ContactDAO(); // Instancie l'objet d'accès aux données
        initializeComponents(); // Initialise les composants de l'interface
        loadContacts(); // Charge les contacts depuis la base de données
    }

    /**
     * Initialise et organise tous les composants de l'interface utilisateur.
     */
    private void initializeComponents() {
        // Crée le panneau pour les champs de saisie
        JPanel inputPanel = createInputPanel();
        
        // Crée le panneau pour la recherche
        JPanel searchPanel = createSearchPanel();
        
        // Crée le panneau de défilement pour la liste des contacts
        JScrollPane contactScrollPane = createContactList();
        
        // Crée le panneau pour les boutons d'action
        JPanel buttonPanel = createButtonPanel();

        // Organisation principale des panneaux dans la fenêtre
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH); // Ajoute le panneau de saisie en haut du panneau supérieur
        topPanel.add(searchPanel, BorderLayout.CENTER); // Ajoute le panneau de recherche au centre du panneau supérieur
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Ajoute une bordure vide pour l'espacement

        add(topPanel, BorderLayout.NORTH); // Ajoute le panneau supérieur en haut de la fenêtre
        add(contactScrollPane, BorderLayout.CENTER); // Ajoute le panneau de défilement de la liste au centre
        add(buttonPanel, BorderLayout.SOUTH); // Ajoute le panneau des boutons en bas
    }

    /**
     * Crée le panneau contenant les champs de saisie pour les informations du contact.
     * @return Le JPanel des champs de saisie.
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5)); // Grille 4x2 avec des espaces
        panel.setBackground(BACKGROUND_COLOR); // Définit la couleur de fond

        // Ajoute les labels et les champs de texte pour chaque information de contact
        addFormField(panel, "Nom :", nameField = new JTextField());
        addFormField(panel, "Prénom :", prenomField = new JTextField());
        addFormField(panel, "Email :", emailField = new JTextField());
        addFormField(panel, "Téléphone :", phoneField = new JTextField());

        return panel;
    }

    /**
     * Méthode utilitaire pour ajouter un label et un champ de texte à un panneau.
     * @param panel Le panneau auquel ajouter les composants.
     * @param label Le texte du label.
     * @param field Le champ de texte à ajouter.
     */
    private void addFormField(JPanel panel, String label, JTextField field) {
        JLabel jLabel = new JLabel(label); // Crée le label
        jLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Définit la police et la taille
        jLabel.setForeground(TEXT_COLOR); // Définit la couleur du texte
        panel.add(jLabel); // Ajoute le label au panneau
        
        field.setFont(new Font("Arial", Font.PLAIN, 16)); // Définit la police et la taille du champ de texte
        panel.add(field); // Ajoute le champ de texte au panneau
    }

    /**
     * Crée le panneau contenant le champ de recherche et les boutons associés.
     * @return Le JPanel de recherche.
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Utilise un BorderLayout avec des espaces
        panel.setBackground(BACKGROUND_COLOR); // Définit la couleur de fond
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Ajoute une bordure vide pour l'espacement

        JLabel label = new JLabel("Rechercher :"); // Crée le label de recherche
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Définit la police et la taille
        label.setForeground(TEXT_COLOR); // Définit la couleur du texte
        panel.add(label, BorderLayout.WEST); // Ajoute le label à gauche

        searchField = new JTextField(); // Crée le champ de texte de recherche
        searchField.setFont(new Font("Arial", Font.PLAIN, 16)); // Définit la police et la taille
        panel.add(searchField, BorderLayout.CENTER); // Ajoute le champ de texte au centre

        // Crée le bouton de recherche et configure son action
        JButton searchBtn = new JButton("Rechercher");
        customizeButton(searchBtn, BUTTON_COLOR); // Personnalise l'apparence du bouton
        searchBtn.addActionListener(e -> searchContacts()); // Ajoute un écouteur d'action pour la recherche

        // Crée le bouton de réinitialisation et configure son action
        JButton resetBtn = new JButton("Réinitialiser");
        customizeButton(resetBtn, new Color(220, 80, 80)); // Personnalise l'apparence du bouton (rougeâtre)
        resetBtn.addActionListener(e -> {
            searchField.setText(""); // Efface le champ de recherche
            loadContacts(); // Recharge tous les contacts
        });

        // Crée un panneau pour les boutons de recherche/réinitialisation
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Grille 1x2 avec espace
        btnPanel.setBackground(BACKGROUND_COLOR); // Définit la couleur de fond
        btnPanel.add(searchBtn); // Ajoute le bouton de recherche
        btnPanel.add(resetBtn); // Ajoute le bouton de réinitialisation
        panel.add(btnPanel, BorderLayout.EAST); // Ajoute ce panneau à droite

        return panel;
    }

    /**
     * Personnalise l'apparence d'un bouton.
     * @param button Le bouton à personnaliser.
     * @param bgColor La couleur de fond du bouton.
     */
    private void customizeButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(120, 40)); // Définit la taille préférée
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Définit la police et la taille
        button.setBackground(bgColor); // Définit la couleur de fond
        button.setForeground(Color.WHITE); // Définit la couleur du texte
        button.setFocusPainted(false); // Désactive l'affichage du contour de focus
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Ajoute une bordure vide pour le padding
    }

    /**
     * Crée le panneau de défilement contenant la liste des contacts.
     * Configure l'apparence et l'écouteur de sélection de la liste.
     * @return Le JScrollPane contenant la JList.
     */
    private JScrollPane createContactList() {
        contactListModel = new DefaultListModel<>(); // Crée le modèle de liste
        contactList = new JList<>(contactListModel); // Crée la liste avec le modèle
        contactList.setBackground(LIST_BACKGROUND); // Définit la couleur de fond
        contactList.setForeground(TEXT_COLOR); // Définit la couleur du texte
        contactList.setFont(new Font("Arial", Font.PLAIN, 16)); // Définit la police et la taille
        contactList.setSelectionBackground(LIST_SELECTION); // Définit la couleur de fond de la sélection
        contactList.setSelectionForeground(Color.WHITE); // Définit la couleur du texte de la sélection

        // Ajoute un écouteur pour les changements de sélection dans la liste
        contactList.addListSelectionListener(e -> {
            // Vérifie si le changement est terminé (pas en cours d'ajustement)
            if (!e.getValueIsAdjusting()) {
                Contact selected = contactList.getSelectedValue(); // Obtient l'élément sélectionné
                if (selected != null) {
                    // Rempli les champs de texte avec les informations du contact sélectionné
                    nameField.setText(selected.getNom());
                    prenomField.setText(selected.getPrenom());
                    emailField.setText(selected.getEmail());
                    phoneField.setText(selected.getTel());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactList); // Crée un panneau de défilement pour la liste
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Ajoute une bordure fine
        return scrollPane; // Retourne le panneau de défilement
    }

    /**
     * Crée le panneau contenant les boutons d'action (Ajouter, Modifier, Supprimer).
     * @return Le JPanel des boutons d'action.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Utilise un FlowLayout centré avec des espaces
        panel.setBackground(BACKGROUND_COLOR); // Définit la couleur de fond
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10)); // Ajoute une bordure vide pour l'espacement

        // Crée le bouton "Ajouter" et configure son action
        JButton addBtn = new JButton("Ajouter");
        customizeButton(addBtn, BUTTON_COLOR); // Personnalise l'apparence
        addBtn.addActionListener(e -> addContact()); // Ajoute l'écouteur d'action

        // Crée le bouton "Modifier" et configure son action
        JButton editBtn = new JButton("Modifier");
        customizeButton(editBtn, BUTTON_COLOR); // Personnalise l'apparence
        editBtn.addActionListener(e -> editContact()); // Ajoute l'écouteur d'action

        // Crée le bouton "Supprimer" et configure son action
        JButton deleteBtn = new JButton("Supprimer");
        customizeButton(deleteBtn, new Color(220, 80, 80)); // Personnalise l'apparence (rougeâtre)
        deleteBtn.addActionListener(e -> deleteContact()); // Ajoute l'écouteur d'action

        // Ajoute les boutons au panneau
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel; // Retourne le panneau des boutons
    }

    /**
     * Effectue une recherche de contacts basée sur le terme saisi dans le champ de recherche.
     * Met à jour la liste des contacts affichée.
     */
    private void searchContacts() {
        String term = searchField.getText().trim(); // Récupère le terme de recherche (sans espaces blancs)
        contactListModel.clear(); // Efface le modèle de liste actuel
        List<Contact> results = term.isEmpty() 
            ? contactDAO.getAllContacts() // Si le terme est vide, affiche tous les contacts
            : contactDAO.searchContacts(term); // Sinon, effectue la recherche
        results.forEach(contactListModel::addElement); // Ajoute les résultats de la recherche au modèle de liste
    }

    /**
     * Ajoute un nouveau contact à la base de données en utilisant les informations
     * des champs de saisie. Valide les champs avant l'ajout.
     */
    private void addContact() {
        // Valide les champs de saisie. Si la validation échoue, la méthode s'arrête.
        if (!validateFields()) return;
        
        // Crée un nouvel objet Contact avec les informations des champs
        Contact contact = new Contact(
            nameField.getText().trim(),
            prenomField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim()
        );

        // Tente d'ajouter le contact via le DAO
        if (contactDAO.addContact(contact)) {
            showSuccess("Contact ajouté avec succès"); // Affiche un message de succès
            clearFields(); // Efface les champs de saisie
            loadContacts(); // Recharge la liste des contacts
        } else {
            // Affiche un message d'erreur si l'ajout a échoué (par exemple, doublon d'email/téléphone)
            showError("Erreur lors de l'ajout. Email ou téléphone existe déjà.");
        }
    }

    /**
     * Modifie les informations du contact actuellement sélectionné dans la liste.
     * Valide les champs avant la modification.
     */
    private void editContact() {
        Contact selected = contactList.getSelectedValue(); // Obtient le contact sélectionné
        if (selected == null) {
            showWarning("Veuillez sélectionner un contact"); // Affiche un avertissement si aucun contact n'est sélectionné
            return;
        }
        
        // Valide les champs de saisie. Si la validation échoue, la méthode s'arrête.
        if (!validateFields()) return;

        // Crée un nouvel objet Contact avec l'ID du contact sélectionné et les nouvelles informations
        Contact updated = new Contact(
            selected.getId(), // Utilise l'ID du contact existant
            nameField.getText().trim(),
            prenomField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim()
        );

        // Tente de mettre à jour le contact via le DAO
        if (contactDAO.updateContact(updated)) {
            showSuccess("Contact modifié avec succès"); // Affiche un message de succès
            clearFields(); // Efface les champs de saisie
            loadContacts(); // Recharge la liste des contacts
        } else {
            // Affiche un message d'erreur si la modification a échoué (par exemple, doublon d'email/téléphone)
            showError("Erreur lors de la modification. Email ou téléphone existe déjà.");
        }
    }

    /**
     * Supprime le contact actuellement sélectionné dans la liste après confirmation.
     */
    private void deleteContact() {
        Contact selected = contactList.getSelectedValue(); // Obtient le contact sélectionné
        if (selected == null) {
            showWarning("Veuillez sélectionner un contact"); // Affiche un avertissement si aucun contact n'est sélectionné
            return;
        }

        // Affiche une boîte de dialogue de confirmation avant de supprimer
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer ce contact ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        // Si l'utilisateur confirme la suppression
        if (confirm == JOptionPane.YES_OPTION) {
            // Tente de supprimer le contact via le DAO en utilisant son ID
            if (contactDAO.deleteContact(selected.getId())) {
                showSuccess("Contact supprimé avec succès"); // Affiche un message de succès
                clearFields(); // Efface les champs de saisie
                loadContacts(); // Recharge la liste des contacts
            } else {
                showError("Erreur lors de la suppression"); // Affiche un message d'erreur si la suppression échoue
            }
        }
    }

    /**
     * Valide les champs de saisie (non vides, format valide pour nom, prénom, email, téléphone).
     * @return true si tous les champs sont valides, false sinon.
     */
    private boolean validateFields() {
        String nom = nameField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String tel = phoneField.getText().trim();

        // Vérifie si les champs obligatoires sont vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || tel.isEmpty()) {
            showWarning("Veuillez remplir tous les champs");
            return false;
        }

        // Valide le format du nom
        if (!isValidName(nom)) {
            showError("Nom invalide (lettres et espaces uniquement)");
            return false;
        }

        // Valide le format du prénom
        if (!isValidName(prenom)) {
            showError("Prénom invalide (lettres et espaces uniquement)");
            return false;
        }

        // Valide le format de l'email
        if (!isValidEmail(email)) {
            showError("Format email invalide");
            return false;
        }

        // Valide le format du numéro de téléphone
        if (!isValidPhone(tel)) {
            showError("Téléphone invalide (8 chiffres requis)");
            return false;
        }

        return true; // Tous les champs sont valides
    }

    /**
     * Charge tous les contacts depuis la base de données et les affiche dans la liste.
     */
    private void loadContacts() {
        contactListModel.clear(); // Efface le modèle de liste actuel
        contactDAO.getAllContacts().forEach(contactListModel::addElement); // Ajoute tous les contacts du DAO au modèle
    }

    /**
     * Efface le contenu de tous les champs de saisie et désélectionne la liste.
     */
    private void clearFields() {
        nameField.setText("");
        prenomField.setText("");
        emailField.setText("");
        phoneField.setText("");
        contactList.clearSelection(); // Désélectionne l'élément dans la liste
    }

    /**
     * Affiche un message de succès dans une boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche un message d'avertissement dans une boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Affiche un message d'erreur dans une boîte de dialogue.
     * @param message Le message à afficher.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Valide si une chaîne de caractères est un nom ou un prénom valide (lettres et espaces).
     * @param name La chaîne à valider.
     * @return true si la chaîne est valide, false sinon.
     */
    private boolean isValidName(String name) {
        return name.matches("[a-zA-ZÀ-ÿ ]+"); // Accepte lettres (majuscules/minuscules, accents) et espaces
    }

    /**
     * Valide si une chaîne de caractères est un format d'email valide.
     * @param email La chaîne à valider.
     * @return true si la chaîne est un email valide, false sinon.
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"); // Regex simple pour un format d'email basique
    }

    /**
     * Valide si une chaîne de caractères est un numéro de téléphone valide (8 chiffres).
     * @param phone La chaîne à valider.
     * @return true si la chaîne est un numéro de téléphone valide, false sinon.
     */
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{8}"); // Accepte exactement 8 chiffres
    }}