document.getElementById("sidebarToggle").addEventListener("click", () => {
  const sidebar = document.getElementById("sidebar-wrapper");
  const body = document.body;
  sidebar.classList.toggle("open");
  body.classList.toggle("sidebar-open");
});

let dateSortAsc = true;
let amountSortAsc = true;

window.sortByDate = () => {
  const tableBody = document.getElementById("expenseTableBody");
  const rows = Array.from(tableBody.querySelectorAll("tr"));
  rows.sort((a, b) => {
    const dateA = new Date(a.cells[0].innerText.trim().split("-").reverse().join("-"));
    const dateB = new Date(b.cells[0].innerText.trim().split("-").reverse().join("-"));
    return dateSortAsc ? dateA - dateB : dateB - dateA;
  });
  rows.forEach(row => tableBody.appendChild(row));
  dateSortAsc = !dateSortAsc;
};

window.sortByAmount = () => {
  const tableBody = document.getElementById("expenseTableBody");
  const rows = Array.from(tableBody.querySelectorAll("tr"));

  rows.sort((a, b) => {
    const amtA = parseFloat(a.cells[3].innerText.replace(/[^\d.-]/g, ""));
    const amtB = parseFloat(b.cells[3].innerText.replace(/[^\d.-]/g, ""));
    return amountSortAsc ? amtA - amtB : amtB - amtA;
  });

  rows.forEach(row => tableBody.appendChild(row));
  amountSortAsc = !amountSortAsc;
};

document.getElementById("searchInput").addEventListener("keyup", function () {
  const filter = this.value.toLowerCase();
  const rows = document.querySelectorAll("#expenseTableBody tr");
  rows.forEach(row => {
    const text = row.textContent.toLowerCase();
    row.style.display = text.includes(filter) ? "" : "none";
  });
});

let expensePieChart;
const expensePieCtx = document.getElementById("expensePieChart")?.getContext("2d");

function renderExpensePieChart(categoryData) {
  if (!expensePieCtx) return;

  const labels = Object.keys(categoryData);
  const amounts = Object.values(categoryData);
  const colors = labels.map(() => `hsl(${Math.random() * 360}, 60%, 60%)`);

  if (expensePieChart) expensePieChart.destroy();

  expensePieChart = new Chart(expensePieCtx, {
    type: "pie",
    data: {
      labels,
      datasets: [{
        data: amounts,
        backgroundColor: colors,
        borderColor: "#fff",
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: "right",
          labels: { color: "#333", font: { size: 14 } }
        },
        title: {
          display: true,
          text: "Expense by Category",
          color: "#212529",
          font: { size: 16, weight: "bold" }
        }
      }
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  const tableBody = document.getElementById("expenseTableBody");
  const addForm = document.getElementById("addExpenseForm");
  const categorySelect = document.getElementById("category");
  const customCategoryDiv = document.getElementById("customCategoryDiv");
  const customCategoryInput = document.getElementById("customCategory");

  categorySelect.addEventListener("change", () => {
    customCategoryDiv.style.display = categorySelect.value === "Other" ? "block" : "none";
    customCategoryInput.required = categorySelect.value === "Other";
  });

  async function fetchExpenseTransactions() {
    const res = await fetch("/api/transactions");
    const data = await res.json();
    const expenseTxns = data.filter(txn => txn.type === "EXPENSE");

    tableBody.innerHTML = "";

    const categoryTotals = {};
    expenseTxns.forEach(txn => {
      const formattedDate = txn.date.split("-").reverse().join("-");
      tableBody.innerHTML += `
        <tr>
          <td>${formattedDate}</td>
          <td>${txn.type}</td>
          <td>${txn.category}</td>
          <td>₹${txn.amount.toFixed(2)}</td>
          <td>${txn.title}</td>
          <td>
            <button class="btn btn-warning edit-btn"
                    data-id="${txn.id}"
                    data-date="${txn.date}"
                    data-type="${txn.type}"
                    data-category="${txn.category}"
                    data-amount="${txn.amount}"
                    data-title="${txn.title}">Edit</button>
            <button class="btn btn-danger delete-btn"
                    data-id="${txn.id}"
                    data-date="${formattedDate}"
                    data-amount="${txn.amount}">Delete</button>
          </td>
        </tr>
      `;
      categoryTotals[txn.category] = (categoryTotals[txn.category] || 0) + txn.amount;
    });

    renderExpensePieChart(categoryTotals);
    attachEditListeners();
    attachDeleteListeners();
  }

  addForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const formData = new FormData(addForm);
    const newTxn = {
      title: formData.get("title"),
      amount: parseFloat(formData.get("amount")),
      date: formData.get("date"),
      category: categorySelect.value === "Other" ? customCategoryInput.value : formData.get("category"),
      type: "EXPENSE"
    };

    fetch("/api/transactions", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newTxn)
    }).then(() => {
      addForm.reset();
      customCategoryDiv.style.display = "none";
      fetchExpenseTransactions();
    });
  });

  document.getElementById("editTransactionForm").onsubmit = async (e) => {
    e.preventDefault();
    const updatedTxn = {
      date: document.getElementById("editDate").value,
      type: document.getElementById("editType").value,
      category: document.getElementById("editCategory").value,
      amount: parseFloat(document.getElementById("editAmount").value),
      title: document.getElementById("editTitle").value
    };
    const id = document.getElementById("editTransactionId").value;

    const response = await fetch(`/api/transactions/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedTxn)
    });

    if (response.ok) {
      bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
      fetchExpenseTransactions();
    } else {
      alert("Failed to update transaction.");
    }
  };

  document.getElementById("confirmDeleteBtn").onclick = async () => {
    const id = document.getElementById("deleteTransactionId").value;

    const response = await fetch(`/api/transactions/${id}`, {
      method: "DELETE"
    });

    if (response.ok) {
      bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
      fetchExpenseTransactions();
    } else {
      alert("Failed to delete transaction.");
    }
  };

  function attachEditListeners() {
    document.querySelectorAll(".edit-btn").forEach(btn => {
      btn.onclick = () => {
        document.getElementById("editTransactionId").value = btn.dataset.id;
        document.getElementById("editDate").value = btn.dataset.date;
        document.getElementById("editType").value = btn.dataset.type;
        document.getElementById("editCategory").value = btn.dataset.category;
        document.getElementById("editAmount").value = btn.dataset.amount;
        document.getElementById("editTitle").value = btn.dataset.title;
        new bootstrap.Modal(document.getElementById("editModal")).show();
      };
    });
  }

  function attachDeleteListeners() {
    document.querySelectorAll(".delete-btn").forEach(btn => {
      btn.onclick = () => {
        const id = btn.dataset.id;
        const date = btn.dataset.date;
        const amount = btn.dataset.amount;
        document.getElementById("deleteTransactionId").value = id;
        document.getElementById("deleteConfirmationText").innerHTML =
          `Are you sure you want to delete the transaction on <strong>${date}</strong> for <strong>₹${amount}</strong>?`;
        new bootstrap.Modal(document.getElementById("deleteModal")).show();
      };
    });
  }

  fetchExpenseTransactions();
});
