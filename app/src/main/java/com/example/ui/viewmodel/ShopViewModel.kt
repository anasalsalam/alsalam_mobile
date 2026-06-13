package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ShopRepository(database.shopDao())

    // --- State Exposures ---
    val users: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parts: StateFlow<List<Part>> = repository.allParts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<Supplier>> = repository.allSupplierPayments.let { repository.allSuppliers }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<Invoice>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val installments: StateFlow<List<Installment>> = repository.allInstallments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierPayments: StateFlow<List<SupplierPayment>> = repository.allSupplierPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repairTickets: StateFlow<List<RepairTicket>> = database.shopDao().getAllRepairTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    var loginError by mutableStateOf<String?>(null)
        private set

    init {
        // Seeding standard data on first open
        viewModelScope.launch {
            seedUserData()
        }
    }

    private suspend fun seedUserData() {
        val currentUsers = database.shopDao().getUserByUsername("admin")
        if (currentUsers == null) {
            // Seed multi-permission employee accounts
            repository.insertUser(User(username = "admin", passwordHash = "admin", role = "admin"))
            repository.insertUser(User(username = "tech", passwordHash = "tech", role = "technician"))
            repository.insertUser(User(username = "cashier", passwordHash = "cashier", role = "cashier"))
            repository.insertUser(User(username = "accountant", passwordHash = "accountant", role = "accountant"))

            // Seed initial phone parts and accessories
            repository.insertPart(Part(code = "P-101", name = "iPhone 14 Pro OLED Display", quantity = 12, costPrice = 120.00, sellingPrice = 199.99, minLimit = 3))
            repository.insertPart(Part(code = "P-102", name = "Samsung S23 Charger USB-C Port", quantity = 25, costPrice = 15.00, sellingPrice = 35.00, minLimit = 5))
            repository.insertPart(Part(code = "P-103", name = "Galaxy Note 20 Charging IC Chip", quantity = 4, costPrice = 8.50, sellingPrice = 22.00, minLimit = 5)) // Trigger stock alert
            repository.insertPart(Part(code = "P-104", name = "Premium Tempered Glass (iPhone)", quantity = 60, costPrice = 1.20, sellingPrice = 10.00, minLimit = 10))
            repository.insertPart(Part(code = "P-105", name = "Silicon Case Armor Universal", quantity = 45, costPrice = 2.50, sellingPrice = 15.00, minLimit = 8))

            // Seed some customers
            repository.insertCustomer(Customer(name = "Ahmad Al-Harbi", phone = "+966501112223", email = "ahmad@mail.com", remainingInstallments = 3, remainingBalance = 150.00))
            repository.insertCustomer(Customer(name = "Fatima Al-Zahrani", phone = "+966502223334", email = "fatima@mail.com", remainingInstallments = 0, remainingBalance = 0.00))
            repository.insertCustomer(Customer(name = "John Doe", phone = "+155501923", email = "john@mail.com", remainingInstallments = 12, remainingBalance = 480.00))

            // Seed initial suppliers
            repository.insertSupplier(Supplier(name = "Future Tech Wholesalers", phone = "011234567", email = "orders@futuretech.com", companyName = "Future Tech Group", outstandingBalance = 1500.00))
            repository.insertSupplier(Supplier(name = "Ali Express Global B2B", phone = "+86208888", email = "b2b@aliexpress.com", companyName = "Alibaba Group", outstandingBalance = 0.00))

            // Seed some default expenses
            repository.insertExpense(Expense(category = "Rent", amount = 800.00, description = "Main shop workshop rental June"))
            repository.insertExpense(Expense(category = "Utilities", amount = 145.50, description = "Electricity & Fiber internet bill"))
            repository.insertExpense(Expense(category = "Wages", amount = 1200.00, description = "Part-time assistant helper payout"))

            // Seed static demo invoices so the charts and reports are fully functional on first run
            repository.createInvoice("Ahmad Al-Harbi", 1, "installment", 50.00, 3, listOf(
                InvoiceLineItem(partId = 1, quantity = 1, sellingPrice = 199.99)
            ))
            repository.createInvoice("Fatima Al-Zahrani", 2, "cash", 35.00, 0, listOf(
                InvoiceLineItem(partId = 2, quantity = 1, sellingPrice = 35.00)
            ))

            // Seed Kanban tasks
            database.shopDao().insertRepairTicket(RepairTicket(ticketId = "TK-101", device = "iPhone 14 Pro", fault = "تغيير شاشة", customerName = "Ahmad Al-Harbi", status = "received"))
            database.shopDao().insertRepairTicket(RepairTicket(ticketId = "TK-102", device = "Samsung S23", fault = "منفذ شحن وتوصيل", customerName = "Fatima Al-Zahrani", status = "checking"))
            database.shopDao().insertRepairTicket(RepairTicket(ticketId = "TK-103", device = "iPad Air 5", fault = "استبدال خلايا بطارية تالفة", customerName = "John Doe", status = "received"))
        }
    }

    fun login(username: String, passwordHash: String): Boolean {
        viewModelScope.launch {
            val user = database.shopDao().getUserByUsername(username)
            if (user != null && user.passwordHash == passwordHash) {
                _currentUser.value = user
                loginError = null
            } else {
                loginError = "اسم المستخدم أو كلمة المرور غير صحيحة"
            }
        }
        return _currentUser.value != null
    }

    fun logout() {
        _currentUser.value = null
    }

    fun registerEmployee(username: String, passwordHash: String, role: String) {
        viewModelScope.launch {
            repository.insertUser(User(username = username, passwordHash = passwordHash, role = role))
        }
    }

    fun removeEmployee(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    // --- Part Actions ---
    fun addPart(code: String, name: String, quantity: Int, costPrice: Double, sellingPrice: Double, minLimit: Int) {
        viewModelScope.launch {
            repository.insertPart(
                Part(
                    code = code,
                    name = name,
                    quantity = quantity,
                    costPrice = costPrice,
                    sellingPrice = sellingPrice,
                    minLimit = minLimit
                )
            )
        }
    }

    fun updatePart(part: Part) {
        viewModelScope.launch {
            repository.updatePart(part)
        }
    }

    fun deletePart(part: Part) {
        viewModelScope.launch {
            repository.deletePart(part)
        }
    }

    // --- Customer CRM ---
    fun addCustomer(name: String, phone: String, email: String) {
        viewModelScope.launch {
            repository.insertCustomer(
                Customer(name = name, phone = phone, email = email)
            )
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    // --- Supplier CRM ---
    fun addSupplier(name: String, phone: String, email: String, companyName: String, initialOwed: Double) {
        viewModelScope.launch {
            repository.insertSupplier(
                Supplier(
                    name = name,
                    phone = phone,
                    email = email,
                    companyName = companyName,
                    outstandingBalance = initialOwed
                )
            )
        }
    }

    fun paySupplier(supplierId: Int, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.insertSupplierPayment(
                SupplierPayment(
                    supplierId = supplierId,
                    amount = amount,
                    notes = notes
                )
            )
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
        }
    }

    // --- Expense Management ---
    fun addExpense(category: String, amount: Double, description: String) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(category = category, amount = amount, description = description)
            )
        }
    }

    fun removeExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // --- Invoice Flow ---
    fun createInvoice(
        customerName: String,
        customerId: Int?,
        type: String, // cash, credit, installment
        paidAmount: Double,
        installmentMonths: Int,
        items: List<InvoiceLineItem>,
        onSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.createInvoice(
                customerName, customerId, type, paidAmount, installmentMonths, items
            )
            onSuccess(result)
        }
    }

    fun payInstallment(installmentId: Int, amount: Double, customerId: Int?) {
        viewModelScope.launch {
            repository.payInstallment(installmentId, amount, customerId)
        }
    }

    // --- Repair Tickets Actions ---
    fun addRepairTicket(device: String, fault: String, customerName: String, notes: String = "") {
        viewModelScope.launch {
            val count = repairTickets.value.size
            val tNum = "TK-${101 + count}"
            database.shopDao().insertRepairTicket(
                RepairTicket(
                    ticketId = tNum,
                    device = device,
                    fault = fault,
                    customerName = customerName,
                    status = "received",
                    notes = notes
                )
            )
        }
    }

    fun updateRepairTicket(ticket: RepairTicket) {
        viewModelScope.launch {
            database.shopDao().updateRepairTicket(ticket)
        }
    }

    fun moveRepairTicketStatus(ticket: RepairTicket, newStatus: String) {
        viewModelScope.launch {
            database.shopDao().updateRepairTicket(ticket.copy(status = newStatus))
        }
    }

    fun deleteRepairTicket(ticket: RepairTicket) {
        viewModelScope.launch {
            database.shopDao().deleteRepairTicket(ticket)
        }
    }
}
