// Profile object
let userProfile = {
    displayName: "",
    email: "",
    avatar: ""
};

// Load profile when page loads (login simulation)
function loadProfile() {
    const savedProfile = localStorage.getItem("userProfile");

    if (savedProfile) {
        userProfile = JSON.parse(savedProfile);

        document.getElementById("displayName").value = userProfile.displayName;
        document.getElementById("email").value = userProfile.email;

        if (userProfile.avatar) {
            document.getElementById("avatarPreview").src = userProfile.avatar;
        }
    }
}

// Save profile
function saveProfile() {

    const displayName = document.getElementById("displayName").value.trim();
    const email = document.getElementById("email").value.trim();

    if (displayName === "") {
        alert("Display name cannot be empty.");
        return;
    }

    if (!email.includes("@")) {
        alert("Please enter a valid email.");
        return;
    }

    userProfile.displayName = displayName;
    userProfile.email = email;

    localStorage.setItem("userProfile", JSON.stringify(userProfile));

    alert("Profile saved successfully!");
}

// Upload profile picture
function uploadAvatar(event) {
    const file = event.target.files[0];

    if (!file) return;

    const reader = new FileReader();

    reader.onload = function(e) {
        const imageData = e.target.result;

        document.getElementById("avatarPreview").src = imageData;
        userProfile.avatar = imageData;
    };

    reader.readAsDataURL(file);
}

// Reset profile
function resetProfile() {

    if (!confirm("Reset profile?")) return;

    localStorage.removeItem("userProfile");

    document.getElementById("displayName").value = "";
    document.getElementById("email").value = "";
    document.getElementById("avatarPreview").src = "default-avatar.png";

    userProfile = {};
}

// Load profile automatically
window.onload = loadProfile;
