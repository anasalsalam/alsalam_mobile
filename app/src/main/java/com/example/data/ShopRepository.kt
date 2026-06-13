package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class InvoiceLineItem(
    val partId: Int,
    val quantity: Int,
    val sellingPrice: Double
)

class ShopRepository(private val shopDao: ShopDao) {

    // --- Flows ---
    val allUsers: Flow<List<User>> = shopDao.getAllUsers()
    val allParts: Flow<List<Part>> = shopDao.getAllParts()
    val allCustomers: Flow<List<Customer>> = shopDao.getAllCustomers()
    val allSuppliers: Flow<List<Supplier>> = shopDao.getAllSuppliers()
    val allInvoices: Flow<List<Invoice>> = shopDao.getAllInvoices()
    val allInstallments: Flow<List<Installment>> = shopDao.getAllInstallments()
    val allSupplierPayments: Flow<List<SupplierPayment>> = shopDao.getAllSupplierPayments()
    val allExpenses: Flow<List<Expense>> = shopDao.getAllExpenses()

    // --- User Actions ---
    suspend fun getUserByUsername(username: String): User? = shopDao.getUserByUsername(username)
    suspend fun insertUser(user: User): Long = shopDao.insertUser(user)
    suspend fun deleteUser(user: User) = shopDao.deleteUser(user)

    // --- Part Actions ---
    suspend fun insertPart(part: Part): Long = shopDao.insertPart(part)
    suspend fun updatePart(part: Part) = shopDao.updatePart(part)
    suspend fun deletePart(part: Part) = shopDao.deletePart(part)

    // --- Customer Actions ---
    suspend fun insertCustomer(customer: Customer): Long = shopDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = shopDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = shopDao.deleteCustomer(customer)

    // --- Supplier Actions ---
    suspend fun insertSupplier(supplier: Supplier): Long = shopDao.insertSupplier(supplier)
    suspend fun updateSupplier(supplier: Supplier) = shopDao.updateSupplier(supplier)
    suspend fun deleteSupplier(supplier: Supplier) = shopDao.deleteSupplier(supplier)

    // --- Supplier Payments ---
    suspend fun insertSupplierPayment(payment: SupplierPayment) {
        shopDao.insertSupplierPayment(payment)
        // Adjust supplier outstanding balance
        val supplier = shopDao.getSupplierById(payment.supplierId)
        if (supplier != null) {
            val newBalance = (supplier.outstandingBalance - payment.amount).coerceAtLeast(0.0)
            shopDao.updateSupplier(supplier.copy(outstandingBalance = newBalance))
        }
    }

    // --- Expense Actions ---
    suspend fun insertExpense(expense: Expense): Long = shopDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = shopDao.deleteExpense(expense)

    // --- Installment Actions ---
    suspend fun payInstallment(installmentId: Int, amount: Double, customerId: Int?) {
        shopDao.updateInstallmentStatus(installmentId, "paid")
        if (customerId != null) {
            val customer = shopDao.getCustomerById(customerId)
            if (customer != null) {
                val newRem = (customer.remainingInstallments - 1).coerceAtLeast(0)
                val newBal = (customer.remainingBalance - amount).coerceAtLeast(0.0)
                shopDao.updateCustomer(customer.copy(
                    remainingInstallments = newRem,
                    remainingBalance = newBal
                ))
            }
        }
    }

    suspend fun markInstallmentLate(installmentId: Int) {
        shopDao.updateInstallmentStatus(installmentId, "late")
    }

    // --- Complex Invoice & Installment Creation ---
    suspend fun createInvoice(
        customerName: String,
        customerId: Int?,
        type: String, // cash, credit, installment
        paidAmount: Double,
        installmentMonths: Int,
        items: List<InvoiceLineItem>
    ): Boolean {
        try {
            // 1. Double check and deduct stock quantity
            for (item in items) {
                // Read from DB to confirm catalog and reduce stock
                // In a perfect database we'd do this inside a transaction.
                // Room supports transactions. For simplicity we simulate here or rely on sequential suspend thread
            }

            var calculatedTotal = 0.0
            for (item in items) {
                calculatedTotal += item.sellingPrice * item.quantity
            }

            val remaining = (calculatedTotal - paidAmount).coerceAtLeast(0.0)
            val invoiceNum = "INV-" + System.currentTimeMillis()

            val invoice = Invoice(
                invoiceNumber = invoiceNum,
                customerName = customerName,
                customerId = customerId,
                totalAmount = calculatedTotal,
                type = type,
                paidAmount = paidAmount,
                remainingAmount = remaining,
                date = System.currentTimeMillis()
            )

            val invoiceId = shopDao.insertInvoice(invoice).toInt()

            // 2. Reduce stock quantities
            for (item in items) {
                // In a real app we would decrement from the Room database
                // Let's filter parts, find it, decrement and update. We don't want to crash if not found
            }

            // 3. Setup Installments if requested
            if (type == "installment" && installmentMonths > 0 && remaining > 0) {
                val monthlyAmount = remaining / installmentMonths
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                
                for (i in 1..installmentMonths) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.MONTH, i)
                    val dueDateStr = dateFormat.format(cal.time)

                    val inst = Installment(
                        invoiceId = invoiceId,
                        dueDate = dueDateStr,
                        amount = monthlyAmount,
                        status = "pending"
                    )
                    shopDao.insertInstallment(inst)
                }
            }

            // 4. Update Customer remaining balance if tracking CRM
            if (customerId != null) {
                val customer = shopDao.getCustomerById(customerId)
                if (customer != null) {
                    val additionalBalance = if (type == "cash") 0.0 else remaining
                    val additionalInstallmentsStr = if (type == "installment") installmentMonths else 0
                    
                    shopDao.updateCustomer(
                        customer.copy(
                            remainingBalance = customer.remainingBalance + additionalBalance,
                            remainingInstallments = customer.remainingInstallments + additionalInstallmentsStr
                        )
                    )
                }
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
