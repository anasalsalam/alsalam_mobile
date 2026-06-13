package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    // --- Users ---
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

    // --- Parts ---
    @Query("SELECT * FROM parts ORDER BY name ASC")
    fun getAllParts(): Flow<List<Part>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: Part): Long

    @Update
    suspend fun updatePart(part: Part)

    @Delete
    suspend fun deletePart(part: Part)

    // --- Customers ---
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Int): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    // --- Suppliers ---
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    suspend fun getSupplierById(id: Int): Supplier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    // --- Invoices ---
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Int): Invoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    // --- Installments ---
    @Query("SELECT * FROM installments ORDER BY dueDate ASC")
    fun getAllInstallments(): Flow<List<Installment>>

    @Query("SELECT * FROM installments WHERE invoiceId = :invoiceId ORDER BY dueDate ASC")
    fun getInstallmentsForInvoice(invoiceId: Int): Flow<List<Installment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(installment: Installment): Long

    @Update
    suspend fun updateInstallment(installment: Installment)

    @Query("UPDATE installments SET status = :status WHERE id = :id")
    suspend fun updateInstallmentStatus(id: Int, status: String)

    // --- Supplier Payments ---
    @Query("SELECT * FROM supplier_payments ORDER BY date DESC")
    fun getAllSupplierPayments(): Flow<List<SupplierPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplierPayment(payment: SupplierPayment): Long

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- Repair Tickets ---
    @Query("SELECT * FROM repair_tickets ORDER BY id DESC")
    fun getAllRepairTickets(): Flow<List<RepairTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairTicket(ticket: RepairTicket): Long

    @Update
    suspend fun updateRepairTicket(ticket: RepairTicket)

    @Delete
    suspend fun deleteRepairTicket(ticket: RepairTicket)
}
