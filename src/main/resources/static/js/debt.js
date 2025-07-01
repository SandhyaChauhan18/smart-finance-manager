document.getElementById("sidebarToggle").addEventListener("click", () => {
  const sidebar = document.getElementById("sidebar-wrapper");
  const body = document.body;
  sidebar.classList.toggle("open");
  body.classList.toggle("sidebar-open");
});

document.addEventListener("DOMContentLoaded", () => {
  fetchDebts();

  document.getElementById("addDebtForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = new FormData(this);
    const data = Object.fromEntries(formData.entries());

    fetch("/api/debt", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(() => {
      bootstrap.Modal.getInstance(document.getElementById("addModal")).hide();
      this.reset();
      fetchDebts();
    });
  });

  document.getElementById("editDebtForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = new FormData(this);
    const data = Object.fromEntries(formData.entries());
    const id = data.id;

    fetch(`/api/debt/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(() => {
      bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
      this.reset();
      fetchDebts();
    });
  });

  document.getElementById("searchInput").addEventListener("keyup", function () {
    const filter = this.value.toLowerCase();
    const rows = document.querySelectorAll("#debtTableBody tr");

    rows.forEach(row => {
      const text = row.textContent.toLowerCase();
      row.style.display = text.includes(filter) ? "" : "none";
    });
  });

  document.getElementById("confirmDeleteBtn").addEventListener("click", async () => {
    const id = document.getElementById("deleteDebtId").value;

    const response = await fetch(`/api/debt/${id}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
      fetchDebts();
    } else {
      alert("Failed to delete the debt.");
    }
  });
});

function fetchDebts() {
  fetch("/api/debt")
    .then(res => res.json())
    .then(data => {
      let rows = "";
      let total = 0, paid = 0, pending = 0;

      data.forEach((debt, i) => {
        total += debt.amount;
        if (debt.status === "PAID") paid += debt.amount;
        else pending += debt.amount;

        const rowClass = debt.status === "PAID" ? 'table-success' : 'custom-pending';

        rows += `
          <tr class="${rowClass}">
            <td>${i + 1}</td>
            <td>${debt.borrowerName}</td>
            <td>₹${debt.amount.toFixed(2)}</td>
            <td>${debt.dateLent}</td>
            <td>${debt.description || '-'}</td>
            <td><span class="badge ${debt.status === 'PAID' ? 'bg-success' : 'bg-warning text-dark'}">${debt.status}</span></td>
            <td class="text-center">
              <button class="btn btn-sm btn-success me-1" onclick="markAsPaid(${debt.id})"><i class="bi bi-check-circle"></i></button>
              <button class="btn btn-sm btn-warning me-1" onclick="openEditModal(${debt.id}, '${debt.borrowerName}', ${debt.amount}, '${debt.dateLent}', \`${debt.description || ''}\`)"><i class="bi bi-pencil-square"></i></button>
              <button class="btn btn-sm btn-danger delete-btn"
                      data-id="${debt.id}"
                      data-name="${debt.borrowerName}"
                      data-amount="${debt.amount.toFixed(2)}">
                <i class="bi bi-trash"></i>
              </button>
            </td>
          </tr>`;
      });

      document.getElementById("debtTableBody").innerHTML = rows;
      document.getElementById("totalLent").textContent = `₹${total.toFixed(2)}`;
      document.getElementById("totalPaid").textContent = `₹${paid.toFixed(2)}`;
      document.getElementById("totalPending").textContent = `₹${pending.toFixed(2)}`;

      document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          const id = btn.dataset.id;
          const name = btn.dataset.name;
          const amount = btn.dataset.amount;

          const text = `Are you sure you want to delete the debt for <strong>${name}</strong> amounting to <strong>₹${amount}</strong>?`;

          document.getElementById('deleteDebtId').value = id;
          document.getElementById('deleteConfirmationText').innerHTML = text;

          new bootstrap.Modal(document.getElementById('deleteModal')).show();
        });
      });
    });
}

function markAsPaid(id) {
  fetch(`/api/debt/${id}/paid`, { method: "PUT" }).then(fetchDebts);
}

function openEditModal(id, borrowerName, amount, dateLent, description) {
  const form = document.getElementById("editDebtForm");
  form.id.value = id;
  form.borrowerName.value = borrowerName;
  form.amount.value = amount;
  form.dateLent.value = dateLent;
  form.description.value = description;

  new bootstrap.Modal(document.getElementById("editModal")).show();
}
