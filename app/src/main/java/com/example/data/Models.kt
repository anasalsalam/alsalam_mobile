package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: String, // admin, technician, cashier, accountant
    val isTwoFactorEnabled: Boolean = false,
    val twoFactorSecret: String = ""
)

@Entity(tableName = "parts")
data class Part(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val name: String,
    val quantity: Int,
    val costPrice: Double,
    val sellingPrice: Double,
    val minLimit: Int = 5
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val remainingInstallments: Int = 0,
    val remainingBalance: Double = 0.0
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val companyName: String = "",
    val outstandingBalance: Double = 0.0
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val customerName: String,
    val customerId: Int? = null,
    val totalAmount: Double,
    val type: String, // cash, credit, installment
    val paidAmount: Double,
    val remainingAmount: Double,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "installments")
data class Installment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: Int,
    val dueDate: String, // YYYY-MM-DD
    val amount: Double,
    val status: String // pending, paid, late
)

@Entity(tableName = "supplier_payments")
data class SupplierPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val supplierId: Int,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // wages, rent, utilities, spare_parts, marketing, other
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val description: String = ""
)

@Entity(tableName = "repair_tickets")
data class RepairTicket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: String,
    val device: String,
    val fault: String,
    val customerName: String,
    val status: String, // received, checking, completed, delivered
    val notes: String = ""
)

