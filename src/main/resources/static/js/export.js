document.getElementById("sidebarToggle").addEventListener("click", () => {
  const sidebar = document.getElementById("sidebar-wrapper");
  const body = document.body;
  sidebar.classList.toggle("open");
  body.classList.toggle("sidebar-open");
});

const exportTypeRadios = document.getElementsByName("exportType");
const monthGroup = document.getElementById("monthGroup");
const yearSelect = document.getElementById("yearSelect");
const monthSelect = document.getElementById("monthSelect");
const exportBtn = document.getElementById("exportBtn");

function populateYearOptions() {
  const currentYear = new Date().getFullYear();
  for (let y = currentYear; y >= currentYear - 4; y--) {
    const option = document.createElement("option");
    option.value = y;
    option.textContent = y;
    yearSelect.appendChild(option);
  }
}

function handleExportTypeChange() {
  const selected = document.querySelector("input[name='exportType']:checked").value;
  if (selected === "monthly") {
    monthGroup.style.display = "block";
  } else {
    monthGroup.style.display = "none";
  }
}

async function handleExportClick() {
  const exportType = document.querySelector("input[name='exportType']:checked").value;
  const year = yearSelect.value;
  let url = "";

  if (!year) {
    alert("Please select a year.");
    return;
  }

  if (exportType === "monthly") {
    const month = monthSelect.value;
    if (!month) {
      alert("Please select a month.");
      return;
    }
    url = `/api/transactions/export/monthly?year=${year}&month=${month}`;
  } else {
    url = `/api/transactions/export/yearly?year=${year}`;
  }

  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error("Export failed");
    }

    const blob = await response.blob();
    const contentDisposition = response.headers.get("Content-Disposition");
    const filenameMatch = contentDisposition && contentDisposition.match(/filename="(.+)"/);
    const filename = filenameMatch ? filenameMatch[1] : "export.csv";

    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();

  } catch (error) {
    console.error("Error during export:", error);
    alert("Something went wrong while exporting the data. Please try again.");
  }
}

exportTypeRadios.forEach(radio =>
  radio.addEventListener("change", handleExportTypeChange)
);
exportBtn.addEventListener("click", handleExportClick);

populateYearOptions();
handleExportTypeChange();
