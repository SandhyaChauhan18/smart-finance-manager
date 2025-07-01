document.getElementById("sidebarToggle").addEventListener("click", () => {
  const sidebar = document.getElementById("sidebar-wrapper");
  const body = document.body;

  sidebar.classList.toggle("open");
  body.classList.toggle("sidebar-open");
   if (incomeExpenseChartInstance) {
          setTimeout(() => incomeExpenseChartInstance.resize(), 300);
      }
});

let incomeExpenseChartInstance;
document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/dashboard/monthly-chart")
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById("incomeExpenseChart").getContext("2d");

            incomeExpenseChartInstance = new Chart(ctx, {
                type: "bar",
                data: {
                    labels: data.months,
                    datasets: [
                        {
                            label: "Income",
                            data: data.income,
                            backgroundColor: "#198754",
                            borderRadius: 8,
                            barThickness: 30
                        },
                        {
                            label: "Expense",
                            data: data.expense,
                            backgroundColor: "#dc3545",
                            borderRadius: 8,
                            barThickness: 30
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                            labels: {
                                boxWidth: 15,
                                padding: 20,
                                font: {
                                    size: 14
                                }
                            }
                        },
                        tooltip: {
                            backgroundColor: '#333',
                            titleColor: '#fff',
                            bodyColor: '#fff'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: {
                                color: '#e0e0e0'
                            },
                            ticks: {
                                stepSize: 500
                            }
                        },
                        x: {
                            grid: {
                                display: false
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error("Chart load error:", error));
});


document.getElementById("searchInput").addEventListener("keyup", function () {
    const filter = this.value.toLowerCase();
    const rows = document.querySelectorAll("#transactionTableBody tr");

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(filter) ? "" : "none";
    });
});

let dateSortAsc = true;
let amountSortAsc = true;

function sortByDate() {
    const tableBody = document.getElementById("transactionTableBody");
    const rows = Array.from(tableBody.querySelectorAll("tr"));

    rows.sort((a, b) => {
        const dateA = new Date(a.cells[0].innerText.trim().split("-").reverse().join("-"));
        const dateB = new Date(b.cells[0].innerText.trim().split("-").reverse().join("-"));

        return dateSortAsc ? dateA - dateB : dateB - dateA;
    });

    rows.forEach(row => tableBody.appendChild(row));
    dateSortAsc = !dateSortAsc;
}

function sortByAmount() {
    const tableBody = document.getElementById("transactionTableBody");
    const rows = Array.from(tableBody.querySelectorAll("tr"));

    rows.sort((a, b) => {
        const amtA = parseFloat(a.cells[3].innerText.replace(/[^\d.-]/g, ""));
        const amtB = parseFloat(b.cells[3].innerText.replace(/[^\d.-]/g, ""));

        return amountSortAsc ? amtA - amtB : amtB - amtA;
    });

    rows.forEach(row => tableBody.appendChild(row));
    amountSortAsc = !amountSortAsc;
}


document.querySelectorAll('.edit-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.getElementById('editTransactionId').value = btn.dataset.id;
    document.getElementById('editDate').value = btn.dataset.date;
    document.getElementById('editType').value = btn.dataset.type;
    document.getElementById('editCategory').value = btn.dataset.category;
    document.getElementById('editAmount').value = btn.dataset.amount;
    document.getElementById('editTitle').value = btn.dataset.title;

    new bootstrap.Modal(document.getElementById('editModal')).show();
  });
});

document.getElementById('editTransactionForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const id = document.getElementById('editTransactionId').value;
  const updatedData = {
    date: document.getElementById('editDate').value,
    type: document.getElementById('editType').value,
    category: document.getElementById('editCategory').value,
    amount: parseFloat(document.getElementById('editAmount').value),
    title: document.getElementById('editTitle').value
  };

  const response = await fetch(`/api/transactions/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updatedData)
  });

  if (response.ok) {
    location.reload();
  } else {
    alert("Failed to update transaction");
  }
});

document.querySelectorAll('.delete-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    const id = btn.dataset.id;
    const text = `Are you sure you want to delete the transaction on <strong>${btn.dataset.date}</strong> for <strong>₹${btn.dataset.amount}</strong>?`;

    document.getElementById('deleteTransactionId').value = id;
    document.getElementById('deleteConfirmationText').innerHTML = text;

    new bootstrap.Modal(document.getElementById('deleteModal')).show();
  });
});

document.getElementById('confirmDeleteBtn').addEventListener('click', async () => {
  const id = document.getElementById('deleteTransactionId').value;

  const response = await fetch(`/api/transactions/${id}`, {
    method: 'DELETE'
  });

  if (response.ok) {
    location.reload();
  } else {
    alert("Failed to delete transaction");
  }
});
