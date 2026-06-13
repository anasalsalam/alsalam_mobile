import React, { useState, useMemo } from 'react';
import {
  PolishButton,
  PolishTextField,
  PolishCard,
  PolishStatsCard,
  PolishTable
} from './UIComponents.jsx';

// SVGs for clean UI and professional aesthetic without external library friction
const PlusIcon = () => (
  <svg className="w-5 h-5 animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
  </svg>
);

const TrashIcon = () => (
  <svg className="w-4 h-4 text-[#B3261E]" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-4v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
  </svg>
);

const SearchIcon = () => (
  <svg className="w-5 h-5 text-[#6750A4]" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
  </svg>
);

const DollarIcon = () => (
  <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0V17m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
  </svg>
);

export default function SupplierManagement() {
  const [role, setRole] = useState('admin'); // 'admin' | 'technician' | 'cashier'
  const [activeTab, setActiveTab] = useState('profiles'); // 'profiles' | 'payments'
  const [searchQuery, setSearchQuery] = useState('');

  // Sample Supplier Profiles
  const [suppliers, setSuppliers] = useState([
    { id: 1, name: 'Riyadh Parts Hub (موزع الرياض الذكي)', contact: 'Ibrahim Al-Fahad', phone: '+966 50 123 4567', email: 'riyadh.parts@domain.sa', balance: 4500.00, suppliedCategory: 'OLED Screens & Glass Panels' },
    { id: 2, name: 'Guangzhou Electronics Co.', contact: 'Chen Wei', phone: '+86 20 8888 9999', email: 'contact@gz-elec.cn', balance: 1280.50, suppliedCategory: 'Micro Chips & IC Boards' },
    { id: 3, name: 'Khobar Repair Supplies Shop', contact: 'Musaed Salem', phone: '+966 54 987 6543', email: 'khobar.supplies@domain.com', balance: 0.00, suppliedCategory: 'Tools & Soldering Iron Accessories' },
    { id: 4, name: 'Jeddah Screen Wholesale Ltd.', contact: 'Sarah Bashir', phone: '+966 56 555 1209', email: 'wholesale.jeddah@screens.sa', balance: 3200.00, suppliedCategory: 'Premium Tablet Displays' }
  ]);

  // Sample Supplier Payments Logged
  const [payments, setPayments] = useState([
    { id: 101, date: '2026-06-05', supplierId: 1, supplierName: 'Riyadh Parts Hub (موزع الرياض الذكي)', amount: 1500.00, method: 'Bank Transfer (تحويل بنكي)', notes: 'Partial payout for iPhone 15 screens supply chain' },
    { id: 102, date: '2026-06-10', supplierId: 2, supplierName: 'Guangzhou Electronics Co.', amount: 2000.00, method: 'Cash (نقدي)', notes: 'Settlement for batch code IC-TRISTAR-3' },
    { id: 103, date: '2026-06-12', supplierId: 3, supplierName: 'Khobar Repair Supplies Shop', amount: 840.00, method: 'Credit Card (بطاقة)', notes: 'Paid in full for 2x soldering stations' }
  ]);

  // Supplier Form State
  const [showAddSupplier, setShowAddSupplier] = useState(false);
  const [supName, setSupName] = useState('');
  const [supContact, setSupContact] = useState('');
  const [supPhone, setSupPhone] = useState('');
  const [supEmail, setSupEmail] = useState('');
  const [supCategory, setSupCategory] = useState('OLED Screens & Glass Panels');
  const [supBalance, setSupBalance] = useState('');

  // Payment Form State
  const [showAddPayment, setShowAddPayment] = useState(false);
  const [paySupplierId, setPaySupplierId] = useState(1);
  const [payAmount, setPayAmount] = useState('');
  const [payMethod, setPayMethod] = useState('Bank Transfer (تحويل بنكي)');
  const [payDate, setPayDate] = useState('2026-06-13');
  const [payNotes, setPayNotes] = useState('');

  const categoiresList = [
    'OLED Screens & Glass Panels',
    'Micro Chips & IC Boards',
    'Tools & Soldering Iron Accessories',
    'Premium Tablet Displays',
    'Batteries & Charging Flex Cables',
    'Miscellaneous Hardware accessories'
  ];

  const isAdmin = role === 'admin';

  // Metrics calculation
  const totalOutstanding = useMemo(() => {
    return suppliers.reduce((sum, s) => sum + s.balance, 0);
  }, [suppliers]);

  const activeCreditorsCount = useMemo(() => {
    return suppliers.filter(s => s.balance > 0).length;
  }, [suppliers]);

  const totalPaymentsMade = useMemo(() => {
    return payments.reduce((sum, p) => sum + p.amount, 0);
  }, [payments]);

  // Handle adding new supplier
  const handleAddSupplier = (e) => {
    e.preventDefault();
    if (!supName || !supContact || !supPhone) {
      alert('Please fill out all mandatory fields / يرجى تعبئة الحقول المطلوبة.');
      return;
    }

    const initialBalance = parseFloat(supBalance) || 0;
    const newSup = {
      id: Date.now(),
      name: supName,
      contact: supContact,
      phone: supPhone,
      email: supEmail || 'N/A',
      suppliedCategory: supCategory,
      balance: initialBalance
    };

    setSuppliers([newSup, ...suppliers]);
    setSupName('');
    setSupContact('');
    setSupPhone('');
    setSupEmail('');
    setSupBalance('');
    setShowAddSupplier(false);
  };

  // Handle recording new payment to adjust balances
  const handleAddPayment = (e) => {
    e.preventDefault();
    const amountVal = parseFloat(payAmount);
    if (!amountVal || amountVal <= 0) {
      alert('Please enter a valid payment amount / يرجى إدخال مبلغ دفع صحيح.');
      return;
    }

    const selectedSup = suppliers.find(s => s.id === Number(paySupplierId));
    if (!selectedSup) {
      alert('Please select a valid supplier / يرجى اختيار المورد.');
      return;
    }

    // Append new payment log
    const newPayment = {
      id: Date.now(),
      date: payDate,
      supplierId: selectedSup.id,
      supplierName: selectedSup.name,
      amount: amountVal,
      method: payMethod,
      notes: payNotes.trim()
    };

    setPayments([newPayment, ...payments]);

    // Debit / Reduce supplier's outstanding balance
    setSuppliers(suppliers.map(s => {
      if (s.id === selectedSup.id) {
        return {
          ...s,
          balance: Math.max(0, s.balance - amountVal)
        };
      }
      return s;
    }));

    // Reset Form
    setPayAmount('');
    setPayNotes('');
    setShowAddPayment(false);
  };

  // Delete Supplier
  const handleDeleteSupplier = (id) => {
    if (!isAdmin) {
      alert('Only administrators are authorized to remove supplier records.');
      return;
    }
    if (window.confirm('Are you sure you want to delete this supplier profile? / هل تريد حذف ملف هذا المورد نهائياً؟')) {
      setSuppliers(suppliers.filter(s => s.id !== id));
    }
  };

  // Filter supplier profiles based on search query
  const filteredSuppliers = useMemo(() => {
    return suppliers.filter(s => 
      s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.contact.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.suppliedCategory.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [suppliers, searchQuery]);

  // Filter payments list based on search query
  const filteredPayments = useMemo(() => {
    return payments.filter(p => 
      p.supplierName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.notes.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.method.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [payments, searchQuery]);

  return (
    <div className="min-h-screen bg-[#FEF7FF] text-[#1D1B20] p-6 font-sans">
      <div className="max-w-7xl mx-auto">
        
        {/* Header Ribbon & Corporate Status Bar */}
        <header className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 pb-5 border-b border-[#CAC4D0]">
          <div>
            <div className="flex items-center space-x-3">
              <span className="p-2 bg-[#6750A4] text-white rounded-xl">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </span>
              <div>
                <h1 className="text-2xl font-black text-[#1D1B20] tracking-tight">
                  SupplierManagement.jsx <span className="text-[#6750A4] font-medium">• CRM Portal</span>
                </h1>
                <p className="text-xs text-[#49454F] mt-0.5">
                  Supply Chain Outlets, Procurement Ledger & Balance Sheets (بوابة إدارة الموردين والمستحقات والاعتمادات)
                </p>
              </div>
            </div>
          </div>
          
          {/* Simulated Access Permission Control */}
          <div className="flex items-center space-x-2 mt-4 md:mt-0 bg-[#F3EDF7] p-1.5 rounded-full border border-[#CAC4D0]">
            <span className="text-xs font-bold text-[#49454F] px-2">Access Level:</span>
            {['admin', 'technician', 'cashier'].map(r => (
              <button
                key={r}
                onClick={() => setRole(r)}
                className={`px-4 py-1 rounded-full text-xs font-bold transition-all ${
                  role === r 
                    ? 'bg-[#6750A4] text-white shadow-sm' 
                    : 'text-[#49454F] hover:bg-[#EADDFF]'
                }`}
              >
                {r.toUpperCase()}
              </button>
            ))}
          </div>
        </header>

        {/* Live Period Navigation Panels */}
        <div className="flex flex-col md:flex-row justify-between items-stretch md:items-center mb-6 gap-4">
          <div className="flex space-x-2 bg-[#F3EDF7] p-1 rounded-xl">
            <button
              onClick={() => setActiveTab('profiles')}
              className={`px-5 py-2 text-sm font-bold rounded-lg transition-colors duration-150 ${
                activeTab === 'profiles' ? 'bg-[#6750A4] text-white' : 'text-[#49454F] hover:bg-[#EADDFF]'
              }`}
            >
              🏢 Supplier Profiles (ملفات الموردين)
            </button>
            <button
              onClick={() => setActiveTab('payments')}
              className={`px-5 py-2 text-sm font-bold rounded-lg transition-colors duration-150 ${
                activeTab === 'payments' ? 'bg-[#6750A4] text-white' : 'text-[#49454F] hover:bg-[#EADDFF]'
              }`}
            >
              💸 Outgoing Payments (سجل الدفعات)
            </button>
          </div>

          <div className="flex-1 max-w-xs md:max-w-md">
            <PolishTextField
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search suppliers, categories, codes..."
              className="!mb-0"
              icon={SearchIcon}
            />
          </div>
        </div>

        {/* Corporate Summary Indicators */}
        <section className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <PolishStatsCard
            title="Total Outstanding / إجمالي المستحقات"
            value={`$${totalOutstanding.toFixed(2)}`}
            subtitle="Current debt balance to vendors"
            valueColor="text-[#B3261E]"
          />
          <PolishStatsCard
            title="Active Creditors / عقود مستحقة"
            value={activeCreditorsCount.toString()}
            subtitle="Suppliers with due balance"
            valueColor="text-[#F57C00]"
          />
          <PolishStatsCard
            title="Payments Liquidated / المسدد للموردين"
            value={`$${totalPaymentsMade.toFixed(2)}`}
            subtitle="Aggregated completed transactions"
            valueColor="text-[#2E7D32]"
            containerColor="bg-[#E8F5E9]"
          />
          <PolishStatsCard
            title="Approved Signatory"
            value={role.toUpperCase()}
            subtitle={isAdmin ? "Full writes & modifications" : "Read Only Workspace mode"}
            valueColor={isAdmin ? "text-[#6750A4]" : "text-[#757575]"}
          />
        </section>

        {/* Main interactive grid content */}
        {activeTab === 'profiles' ? (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Form to add or register suppliers */}
            <section className="lg:col-span-1">
              <PolishCard className="bg-white border-[#CAC4D0]">
                <div className="flex items-center justify-between mb-4 border-b pb-2">
                  <h3 className="text-base font-black text-[#6750A4]">
                    🆕 Register Supplier / تسجيل مورد جديد
                  </h3>
                  <span className="text-3xs font-black bg-purple-100 text-[#6750A4] px-2 py-0.5 rounded">
                    CRM Engine
                  </span>
                </div>

                <form onSubmit={handleAddSupplier} className="space-y-4">
                  <PolishTextField
                    label="Supplier Shop/Company Name (اسم الشركة الموردة)*"
                    placeholder="e.g. Riyadh Screen Hub"
                    value={supName}
                    onChange={(e) => setSupName(e.target.value)}
                    required
                  />

                  <PolishTextField
                    label="Contact Account Manager (اسم مندوب المورد)*"
                    placeholder="e.g. Ibrahim Al-Fahad"
                    value={supContact}
                    onChange={(e) => setSupContact(e.target.value)}
                    required
                  />

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    <PolishTextField
                      label="Contact Phone (رقم التواصل)*"
                      placeholder="e.g. +966 50 123 4567"
                      value={supPhone}
                      onChange={(e) => setSupPhone(e.target.value)}
                      required
                    />
                    <PolishTextField
                      label="Vendor Email (البريد الإلكتروني)"
                      type="email"
                      placeholder="e.g. info@riyadhparts.sa"
                      value={supEmail}
                      onChange={(e) => setSupEmail(e.target.value)}
                    />
                  </div>

                  <div className="flex flex-col w-full mb-3">
                    <label className="text-xs font-bold mb-1.5 text-[#49454F]">
                      Core Supplies Class (تصنيف المواد الموردة)*
                    </label>
                    <select
                      value={supCategory}
                      onChange={(e) => setSupCategory(e.target.value)}
                      className="block w-full rounded-lg border border-[#CAC4D0] px-3.5 py-3 text-sm text-[#1D1B20] bg-white transition-all focus:outline-none focus:ring-2 focus:ring-[#6750A4]/20 focus:border-[#6750A4]"
                      style={{ minHeight: '48px' }}
                    >
                      {categoiresList.map((cat, idx) => (
                        <option key={idx} value={cat}>{cat}</option>
                      ))}
                    </select>
                  </div>

                  <PolishTextField
                    label="Initial Outstanding Balance (الرصيد الافتتاحي المستحق $)"
                    type="number"
                    step="0.01"
                    placeholder="e.g. 1500.00"
                    value={supBalance}
                    onChange={(e) => setSupBalance(e.target.value)}
                    helperText="Fill out if there is an existing unpaid balance due."
                  />

                  <div className="pt-2">
                    <PolishButton
                      type="submit"
                      variant="primary"
                      className="w-full animate-bounce-short"
                      icon={PlusIcon}
                    >
                      Create Supplier Profile
                    </PolishButton>
                  </div>
                </form>
              </PolishCard>
            </section>

            {/* List Table of Supplier profiles */}
            <section className="lg:col-span-2">
              <div className="bg-white rounded-2xl border border-[#CAC4D0] p-5">
                <div className="mb-4">
                  <h3 className="text-base font-black text-[#1D1B20]">
                    🏭 Supplier Directory & Ledger Balance (سجل حسابات الموردين)
                  </h3>
                  <p className="text-3xs text-[#757575]">
                    Click individual trash icons to void or unlink. Double click entries for verification.
                  </p>
                </div>

                <PolishTable
                  headers={['Company / المورد', 'Core Supplies', 'Contact Details', 'Outstanding Debt / المديونية']}
                  weights={[25, 25, 30, 20]}
                  rows={filteredSuppliers.map(sup => ({
                    company: (
                      <div>
                        <p className="font-bold text-xs text-[#1D1B20]">{sup.name}</p>
                        <p className="text-3xs text-[#757575]">{sup.contact}</p>
                      </div>
                    ),
                    supplies: (
                      <span className="text-3xs font-black uppercase text-[#6750A4] bg-[#EADDFF] px-2 py-0.5 rounded">
                        {sup.suppliedCategory.split(' ')[0]}
                      </span>
                    ),
                    contact: (
                      <div>
                        <p className="text-xs text-[#1D1B20] font-mono">{sup.phone}</p>
                        <p className="text-3xs text-gray-400 font-mono">{sup.email}</p>
                      </div>
                    ),
                    debt: (
                      <div className="flex items-center justify-between">
                        <span className={`text-xs font-black ${sup.balance > 0 ? 'text-[#B3261E]' : 'text-[#2E7D32]'}`}>
                          ${sup.balance.toFixed(2)}
                        </span>
                        {isAdmin && (
                          <button
                            onClick={() => handleDeleteSupplier(sup.id)}
                            className="p-1.5 hover:bg-red-50 rounded"
                            title="Unlink supplier profile"
                          >
                            <TrashIcon />
                          </button>
                        )}
                      </div>
                    )
                  }))}
                />

                <div className="mt-4 flex justify-between items-center text-3xs text-[#757575] font-semibold">
                  <span>Displaying {filteredSuppliers.length} supplier profiles.</span>
                  <span>Total Liability: <b className="text-[#B3261E] font-black">${totalOutstanding.toFixed(2)}</b></span>
                </div>
              </div>
            </section>

          </div>
        ) : (
          /* Payment Logger view tab */
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

            {/* Logger Panel Form */}
            <section className="lg:col-span-1">
              <PolishCard className="bg-white border-[#CAC4D0]">
                <div className="flex items-center justify-between mb-4 border-b pb-2">
                  <h3 className="text-base font-black text-[#2E7D32]">
                    💵 Log Supplier Payout / قيد دفعة مالية
                  </h3>
                  <span className="text-3xs font-black bg-green-100 text-[#2E7D32] px-2 py-0.5 rounded">
                    Ledger Debit
                  </span>
                </div>

                <form onSubmit={handleAddPayment} className="space-y-4">
                  <div className="flex flex-col w-full mb-3">
                    <label className="text-xs font-bold mb-1.5 text-[#49454F]">
                      Target Supplier (المورد المستحق)*
                    </label>
                    <select
                      value={paySupplierId}
                      onChange={(e) => setPaySupplierId(e.target.value)}
                      className="block w-full rounded-lg border border-[#CAC4D0] px-3.5 py-3 text-sm text-[#1D1B20] bg-white transition-all focus:outline-none focus:ring-2 focus:ring-[#6750A4]/20 focus:border-[#6750A4]"
                      style={{ minHeight: '48px' }}
                    >
                      {suppliers.map(s => (
                        <option key={s.id} value={s.id}>{s.name} (Bal: ${s.balance.toFixed(2)})</option>
                      ))}
                    </select>
                  </div>

                  <PolishTextField
                    label="Payment Amout Paid (مبلغ الدفعة $)*"
                    type="number"
                    step="0.01"
                    placeholder="e.g. 500.00"
                    value={payAmount}
                    onChange={(e) => setPayAmount(e.target.value)}
                    required
                  />

                  <div className="flex flex-col w-full mb-3">
                    <label className="text-xs font-bold mb-1.5 text-[#49454F]">
                      Payment Method (طريقة الصرف السداد)*
                    </label>
                    <select
                      value={payMethod}
                      onChange={(e) => setPayMethod(e.target.value)}
                      className="block w-full rounded-lg border border-[#CAC4D0] px-3.5 py-3 text-sm text-[#1D1B20] bg-white transition-all focus:outline-none focus:ring-2 focus:ring-[#6750A4]/20 focus:border-[#6750A4]"
                      style={{ minHeight: '48px' }}
                    >
                      <option value="Bank Transfer (تحويل بنكي)">Bank Transfer (تحويل بنكي)</option>
                      <option value="Cash (نقدي)">Cash (نقدي)</option>
                      <option value="Credit Card (بطاقة)">Credit Card (بطاقة)</option>
                      <option value="Cheque (شيك)">Cheque (شيك مؤجل)</option>
                    </select>
                  </div>

                  <PolishTextField
                    label="Execution Date (تاريخ سداد الدفعة)*"
                    type="date"
                    value={payDate}
                    onChange={(e) => setPayDate(e.target.value)}
                    required
                  />

                  <PolishTextField
                    label="Memo/Reference Note (البيان ورقم التحويل)"
                    placeholder="e.g. TxID #81928, final settlement for LCD batch"
                    value={payNotes}
                    onChange={(e) => setPayNotes(e.target.value)}
                  />

                  <div className="pt-2 animate-pulse-short">
                    <PolishButton
                      type="submit"
                      variant="primary"
                      className="w-full !bg-[#2E7D32] hover:!bg-[#1B5E20]"
                      icon={DollarIcon}
                    >
                      Log Payment Authorization
                    </PolishButton>
                  </div>
                </form>
              </PolishCard>
            </section>

            {/* Table of outgoing supplier payments */}
            <section className="lg:col-span-2">
              <div className="bg-white rounded-2xl border border-[#CAC4D0] p-5">
                <div className="mb-4">
                  <h3 className="text-base font-black text-[#1D1B20]">
                    💸 Logbook of Procurement Expenditures (سجل الصرف والتسويات)
                  </h3>
                  <p className="text-3xs text-[#757575]">
                    Authorized money transfers debited against existing credit balances.
                  </p>
                </div>

                <PolishTable
                  headers={['Date / تاريخ', 'Paid To / المورد المستفيد', 'Method / الطريقة', 'Amount Paid / القيمة', 'Memo / ملاحظات']}
                  weights={[15, 30, 20, 15, 20]}
                  rows={filteredPayments.map(p => ({
                    date: (
                      <span className="text-xs font-mono text-[#49454F]">{p.date}</span>
                    ),
                    supplier: (
                      <p className="font-bold text-xs text-[#1D1B20]">{p.supplierName}</p>
                    ),
                    method: (
                      <span className="text-3xs font-bold text-green-700 bg-green-50 px-2 py-0.5 rounded border border-green-200">
                        {p.method.split(' ')[0]}
                      </span>
                    ),
                    amount: (
                      <span className="text-xs font-black text-[#2E7D32]">
                        -${p.amount.toFixed(2)}
                      </span>
                    ),
                    notes: (
                      <p className="text-3xs font-semibold text-gray-500 max-w-[150px] overflow-hidden text-ellipsis whitespace-nowrap" title={p.notes}>
                        {p.notes || 'None'}
                      </p>
                    )
                  }))}
                />

                <div className="mt-4 flex justify-between items-center text-3xs text-[#757575] font-semibold">
                  <span>Displaying {filteredPayments.length} payouts logged.</span>
                  <span>Grand Liquidated Capital: <b className="text-[#2E7D32] font-black">${totalPaymentsMade.toFixed(2)}</b></span>
                </div>
              </div>
            </section>

          </div>
        )}

      </div>
    </div>
  );
}
