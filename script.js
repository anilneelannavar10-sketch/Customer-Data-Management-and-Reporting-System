// ============================================================
// Customer Data Management System - Frontend Logic
// Talks to the Java backend REST API at /api/customers
// ============================================================

const API_BASE = "/api/customers";

const form = document.getElementById("customer-form");
const formTitle = document.getElementById("form-title");
const submitBtn = document.getElementById("submit-btn");
const cancelBtn = document.getElementById("cancel-edit-btn");
const formError = document.getElementById("form-error");
const tableBody = document.getElementById("customer-table-body");
const emptyState = document.getElementById("empty-state");
const searchInput = document.getElementById("search-input");

let editingId = null;

// ------------------------------------------------------------------
// Load data on page start
// ------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", () => {
    loadCustomers();
    loadReport();
});

// ------------------------------------------------------------------
// Fetch + render customer list
// ------------------------------------------------------------------
async function loadCustomers(query = "") {
    try {
        const url = query ? `${API_BASE}?q=${encodeURIComponent(query)}` : API_BASE;
        const res = await fetch(url);
        const customers = await res.json();
        renderTable(customers);
    } catch (err) {
        console.error("Failed to load customers:", err);
    }
}

function renderTable(customers) {
    tableBody.innerHTML = "";
    emptyState.classList.toggle("hidden", customers.length > 0);

    customers.forEach((c) => {
        const row = document.createElement("tr");
        const statusClass = c.status === "Active" ? "status-active" : "status-inactive";

        row.innerHTML = `
            <td data-label="ID">${c.id}</td>
            <td data-label="Name">${escapeHtml(c.firstName)} ${escapeHtml(c.lastName)}</td>
            <td data-label="Email">${escapeHtml(c.email)}</td>
            <td data-label="Phone">${escapeHtml(c.phone || "-")}</td>
            <td data-label="City">${escapeHtml(c.city || "-")}</td>
            <td data-label="Status"><span class="status-badge ${statusClass}">${c.status}</span></td>
            <td data-label="Actions">
                <button class="small" onclick="editCustomer(${c.id})">Edit</button>
                <button class="small danger" onclick="deleteCustomer(${c.id})">Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

// ------------------------------------------------------------------
// Summary report
// ------------------------------------------------------------------
async function loadReport() {
    try {
        const res = await fetch(`${API_BASE}/report`);
        const report = await res.json();
        document.getElementById("stat-total").textContent = report.totalCustomers;
        document.getElementById("stat-active").textContent = report.active;
        document.getElementById("stat-inactive").textContent = report.inactive;

        const cityDiv = document.getElementById("city-breakdown");
        cityDiv.innerHTML = report.byCity
            .map((c) => `<span>${escapeHtml(c.city || "Unknown")}: ${c.count}</span>`)
            .join("");
    } catch (err) {
        console.error("Failed to load report:", err);
    }
}

// ------------------------------------------------------------------
// Form submit (add or update)
// ------------------------------------------------------------------
form.addEventListener("submit", async (e) => {
    e.preventDefault();
    formError.classList.add("hidden");

    const payload = {
        firstName: document.getElementById("firstName").value.trim(),
        lastName: document.getElementById("lastName").value.trim(),
        email: document.getElementById("email").value.trim(),
        phone: document.getElementById("phone").value.trim(),
        address: document.getElementById("address").value.trim(),
        city: document.getElementById("city").value.trim(),
        status: document.getElementById("status").value,
    };

    try {
        let res;
        if (editingId) {
            res = await fetch(`${API_BASE}/${editingId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
        } else {
            res = await fetch(API_BASE, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
        }

        const data = await res.json();
        if (!res.ok) {
            showError(data.error || "Something went wrong.");
            return;
        }

        resetForm();
        loadCustomers(searchInput.value.trim());
        loadReport();
    } catch (err) {
        showError("Could not reach the server. Is it running?");
    }
});

function showError(message) {
    formError.textContent = message;
    formError.classList.remove("hidden");
}

// ------------------------------------------------------------------
// Edit / cancel edit
// ------------------------------------------------------------------
async function editCustomer(id) {
    const res = await fetch(API_BASE);
    const customers = await res.json();
    const c = customers.find((cust) => cust.id === id);
    if (!c) return;

    editingId = id;
    document.getElementById("firstName").value = c.firstName;
    document.getElementById("lastName").value = c.lastName;
    document.getElementById("email").value = c.email;
    document.getElementById("phone").value = c.phone || "";
    document.getElementById("address").value = c.address || "";
    document.getElementById("city").value = c.city || "";
    document.getElementById("status").value = c.status;

    formTitle.textContent = "Edit Customer";
    submitBtn.textContent = "Update Customer";
    cancelBtn.classList.remove("hidden");
    window.scrollTo({ top: 0, behavior: "smooth" });
}

cancelBtn.addEventListener("click", resetForm);

function resetForm() {
    form.reset();
    editingId = null;
    formTitle.textContent = "Add New Customer";
    submitBtn.textContent = "Add Customer";
    cancelBtn.classList.add("hidden");
    formError.classList.add("hidden");
}

// ------------------------------------------------------------------
// Delete
// ------------------------------------------------------------------
async function deleteCustomer(id) {
    if (!confirm("Delete this customer record?")) return;
    await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
    loadCustomers(searchInput.value.trim());
    loadReport();
}

// ------------------------------------------------------------------
// Search (debounced)
// ------------------------------------------------------------------
let searchTimeout;
searchInput.addEventListener("input", () => {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => loadCustomers(searchInput.value.trim()), 300);
});

// ------------------------------------------------------------------
// Helpers
// ------------------------------------------------------------------
function escapeHtml(str) {
    if (str === null || str === undefined) return "";
    const div = document.createElement("div");
    div.textContent = str;
    return div.innerHTML;
}
