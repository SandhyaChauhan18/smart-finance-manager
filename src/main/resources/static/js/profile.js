document.getElementById("sidebarToggle").addEventListener("click", () => {
  const sidebar = document.getElementById("sidebar-wrapper");
  const body = document.body;

  sidebar.classList.toggle("open");
  body.classList.toggle("sidebar-open");
   if (incomeExpenseChartInstance) {
          setTimeout(() => incomeExpenseChartInstance.resize(), 300);
      }
});


document.addEventListener("DOMContentLoaded", () => {
  fetch('/api/user')
    .then(res => res.json())
    .then(user => {
      document.getElementById('nameText').textContent = user.name;
      document.getElementById('emailText').textContent = user.email;
      document.getElementById('phoneText').textContent = user.phoneNumber || "Not Provided";
      document.getElementById('addressText').textContent = user.address;

      const photoPath = user.profilePhoto ? '/' + user.profilePhoto : '/images/default-avatar.png';
      document.getElementById('profilePhotoPreview').src = photoPath;

      const form = document.getElementById('updateProfileForm');
      form.name.value = user.name;
      form.phoneNumber.value = user.phoneNumber;
      form.address.value = user.address;
    });

  document.getElementById('updateProfileForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const formData = new FormData(this);

    fetch('/api/user', {
      method: 'PUT',
      body: formData
    })
      .then(res => res.ok ? res.text() : Promise.reject("Update failed"))
      .then(msg => {
        alert(msg);
        location.reload();
      })
      .catch(err => alert("Error: " + err));
  });

  document.getElementById('photoForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const formData = new FormData();
    const fileInput = this.querySelector('input[name="photo"]');
    formData.append('photo', fileInput.files[0]);

    formData.append('name', document.getElementById('nameText').textContent);
    formData.append('phoneNumber', document.getElementById('phoneText').textContent);
    formData.append('address', document.getElementById('addressText').textContent);

    fetch('/api/user', {
      method: 'PUT',
      body: formData
    })
      .then(res => res.ok ? res.text() : Promise.reject("Upload failed"))
      .then(msg => {
        alert(msg);
        location.reload();
      })
      .catch(err => alert("Error: " + err));
  });
});
