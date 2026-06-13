import React, { useState, useMemo } from 'react';
import {
  PolishButton,
  PolishTextField,
  PolishCard,
  PolishStatsCard,
  PolishTable
} from './UIComponents.jsx';

// Inline simple SVG icons to eliminate third-party dependency errors and provide high aesthetic polish
const TrashIcon = () => (
  <svg className="w-5 h-5 text-[#B3261E]" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-4v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
  </svg>
);

const CalendarIcon = () => (
  <svg className="w-5 h-5 text-[#6750A4]" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
  </svg>
);

const TrendingUpIcon = () => (
  <svg className="w-6 h-6 text-[#6750A4]" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 7h8m0 0v8m0-8l-9 9-4-4-6 6" />
  </svg>
);

const PlusIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
  </svg>
);

const FilterIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
  </svg>
);

const DownloadIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
  </svg>
);

export default function ExpenseReporter() {
  const [role, setRole] = useState('admin'); // 'admin' | 'technician' | 'cashier'
  const [activeTab, setActiveTab ] = useState('log'); // 'log' | 'report'

  // Pre-populated standard smart repair operational expenses for June 2026
  const [expenses, setExpenses] = useState([
    { id: 1, category: 'Inventory (قطع غيار)', amount: 1450.00, date: '2026-06-02', description: 'Bought 10x iPhone 15 Screens & OLED modules', loggedBy: 'Admin Ibrahim' },
    { id: 2, category: 'Utilities (مستلزمات عامة)', amount: 220.50, date: '2026-06-05', description: 'High-speed fiber internet and shop water bills', loggedBy: 'Admin Ibrahim' },
    { id: 3, category: 'Rent (إيجار المحل)', amount: 2500.00, date: '2026-06-01', description: 'Monthly main facility rent payment', loggedBy: 'System' },
    { id: 4, category: 'Tools (معدات صيانة)', amount: 480.00, date: '2026-06-08', description: 'Bought 2x professional soldering stations and tweezers', loggedBy: 'Tech Omar' },
    { id: 5, category: 'Inventory (قطع غيار)', amount: 980.00, date: '2026-06-10', description: 'Samsung S24 charging ports & glass panels', loggedBy: 'Admin Ibrahim' },
    { id: 6, category: 'Payroll (رواتب موظفين)', amount: 3200.00, date: '2026-05-31', description: 'May maintenance team base salaries', loggedBy: 'Admin Ibrahim' },
    { id: 7, category: 'Marketing (تسويق وعروض)', amount: 350.00, date: '2026-06-11', description: 'Local Google Map & social media ads budget', loggedBy: 'Admin Ibrahim' },
    { id: 8, category: 'Utilities (مستلزمات عامة)', amount: 180.00, date: '2026-06-12', description: 'Electric energy bill supply', loggedBy: 'System' },
  ]);

  // Form State
  const [amount, setAmount] = useState('');
  const [category, setCategory] = useState('Inventory (قطع غيار)');
  const [date, setDate] = useState('2026-06-13');
  const [description, setDescription] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCategory, setFilterCategory] = useState('All');
  const [selectedMonth, setSelectedMonth] = useState('2026-06'); // Format: YYYY-MM
  const [formError, setFormError] = useState('');

  const categories = [
    'Inventory (قطع غيار)',
    'Utilities (مستلزمات عامة)',
    'Rent (إيجار المحل)',
    'Tools (معدات صيانة)',
    'Payroll (رواتب موظفين)',
    'Marketing (تسويق وعروض)',
    'Miscellaneous (مصاريف أخرى)'
  ];

  // Check if role is admin to allow actions
  const isAdmin = role === 'admin';

  // Handle addition of new logged expense record
  const handleAddExpense = (e) => {
    e.preventDefault();
    setFormError('');

    if (!isAdmin) {
      setFormError('Access Denied: Only administrators are authorized to log operational expenses. / غير مسموح: الصلاحية مخصصة للمدير المالي أو المشرف.');
      return;
    }

    const parsedAmount = parseFloat(amount);
    if (!parsedAmount || parsedAmount <= 0) {
      setFormError('Please enter a valid amount greater than zero. / يرجى إدخال مبلغ صحيح أكبر من الصفر.');
      return;
    }

    if (!date) {
      setFormError('Please select a valid expense date. / يرجى تحديد تاريخ المصروف.');
      return;
    }

    if (!description.trim()) {
      setFormError('Please write a short descriptive note. / يرجى إدخال وصف مبسط لتفاصيل المصروف.');
      return;
    }

    const newExpense = {
      id: Date.now(),
      category,
      amount: parsedAmount,
      date,
      description: description.trim(),
      loggedBy: 'Admin Ibrahim'
    };

    setExpenses([newExpense, ...expenses]);
    
    // Clear inputs safely
    setAmount('');
    setDescription('');
    setFormError('');
  };

  // Remove expense record
  const handleDeleteExpense = (id) => {
    if (!isAdmin) {
      alert('Forbidden: Only administrators can void expense disbursals.');
      return;
    }
    if (window.confirm('Are you sure you want to void this operational expense disbursal? / هل أنت متأكد من إلغاء قيد هذا الصرف المالي؟')) {
      setExpenses(expenses.filter(item => item.id !== id));
    }
  };

  // Expense Filtering Logics
  const filteredAndSearchedExpenses = useMemo(() => {
    return expenses.filter(exp => {
      const matchSearch = exp.description.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          exp.category.toLowerCase().includes(searchQuery.toLowerCase());
      const matchCategory = filterCategory === 'All' || exp.category === filterCategory;
      const matchMonth = exp.date.startsWith(selectedMonth);
      return matchSearch && matchCategory && matchMonth;
    });
  }, [expenses, searchQuery, filterCategory, selectedMonth]);

  // Financial Metrics of selected month
  const metrics = useMemo(() => {
    const currentMonthExpenses = expenses.filter(exp => exp.date.startsWith(selectedMonth));
    const totalSpent = currentMonthExpenses.reduce((sum, item) => sum + item.amount, 0);
    
    // Group totals by categories
    const categoryTotals = {};
    categories.forEach(c => { categoryTotals[c] = 0; });
    currentMonthExpenses.forEach(exp => {
      if (categoryTotals[exp.category] !== undefined) {
        categoryTotals[exp.category] += exp.amount;
      } else {
        categoryTotals[exp.category] = exp.amount;
      }
    });

    const highestCategory = Object.keys(categoryTotals).reduce((a, b) => categoryTotals[a] > categoryTotals[b] ? a : b, categories[0]);
    const highestAmount = categoryTotals[highestCategory] || 0;

    return {
      totalSpent,
      averageExpense: currentMonthExpenses.length ? (totalSpent / currentMonthExpenses.length) : 0,
      highestCategory,
      highestAmount,
      categoryBreakdown: categoryTotals
    };
  }, [expenses, selectedMonth]);

  // Chronological Daily Trend Data for selected month
  const trendData = useMemo(() => {
    // Generate dates in correct month range
    const [year, month] = selectedMonth.split('-');
    const daysInMonth = new Date(year, month, 0).getDate();
    
    const dailyMap = {};
    for (let d = 1; d <= daysInMonth; d++) {
      const dayStr = `${year}-${month}-${String(d).padStart(2, '0')}`;
      dailyMap[dayStr] = 0;
    }

    expenses.forEach(exp => {
      if (exp.date.startsWith(selectedMonth)) {
        if (dailyMap[exp.date] !== undefined) {
          dailyMap[exp.date] += exp.amount;
        }
      }
    });

    return Object.entries(dailyMap)
      .map(([date, amount]) => ({ date, amount }))
      .sort((a, b) => a.date.localeCompare(b.date));
  }, [expenses, selectedMonth]);

  // Max value in trend for graphing scale
  const maxDailySpend = useMemo(() => {
    const maxVal = Math.max(...trendData.map(d => d.amount), 0);
    return maxVal === 0 ? 1000 : maxVal;
  }, [trendData]);

  // Simple Export mockup
  const triggerDummyExport = () => {
    alert(`CSV Report compiled successfully!\nMonth: ${selectedMonth}\nTotal Operational Outpost: $${metrics.totalSpent.toFixed(2)}\nDownloaded in root workspace.`);
  };

  return (
    <div className="min-h-screen bg-[#FEF7FF] text-[#1D1B20] p-6 font-sans">
      <div className="max-w-7xl mx-auto">
        
        {/* Header and Corporate Branding */}
        <header className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 pb-5 border-b border-[#CAC4D0]">
          <div>
            <div className="flex items-center space-x-3">
              <span className="p-2 bg-[#B3261E] text-white rounded-xl">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 002 2h2a2 2 0 002-2z" />
                </svg>
              </span>
              <div>
                <h1 className="text-2xl font-black text-[#1D1B20] tracking-tight">
                  ExpenseReporter.jsx <span className="text-[#B3261E] font-medium">• Smart Repair</span>
                </h1>
                <p className="text-xs text-[#49454F] mt-0.5">
                  General Cash Disbursal, Operational Invoicing & Budget Trend Metrics (بوابة إدارة المصروفات والمحاسبة)
                </p>
              </div>
            </div>
          </div>
          
          {/* Mock Role Switcher (matching standard platform layout for testing security logic) */}
          <div className="flex items-center space-x-2 mt-4 md:mt-0 bg-[#F3EDF7] p-1.5 rounded-full border border-[#CAC4D0]">
            <span className="text-xs font-bold text-[#49454F] px-2">Access Role:</span>
            {['admin', 'technician', 'cashier'].map(r => (
              <button
                key={r}
                onClick={() => setRole(r)}
                className={`px-4 py-1 rounded-full text-xs font-bold transition-all ${
                  role === r 
                    ? 'bg-[#B3261E] text-white shadow-sm' 
                    : 'text-[#49454F] hover:bg-[#EADDFF]'
                }`}
              >
                {r.toUpperCase()}
              </button>
            ))}
          </div>
        </header>

        {/* Dynamic Period Bar and Section Tab Switcher */}
        <div className="flex flex-col md:flex-row justify-between items-stretch md:items-center mb-6 gap-4">
          <div className="flex space-x-2 bg-[#F3EDF7] p-1 rounded-xl">
            <button
              onClick={() => setActiveTab('log')}
              className={`px-5 py-2 text-sm font-bold rounded-lg transition-colors duration-150 ${
                activeTab === 'log' ? 'bg-[#6750A4] text-white' : 'text-[#49454F] hover:bg-[#EADDFF]'
              }`}
            >
              📝 Manage & Disburse
            </button>
            <button
              onClick={() => setActiveTab('report')}
              className={`px-5 py-2 text-sm font-bold rounded-lg transition-colors duration-150 ${
                activeTab === 'report' ? 'bg-[#6750A4] text-white' : 'text-[#49454F] hover:bg-[#EADDFF]'
              }`}
            >
              📈 Monthly Spending Trends
            </button>
          </div>

          {/* Month Scope Filters */}
          <div className="flex items-center space-x-3 bg-white border border-[#CAC4D0] rounded-xl px-3 py-1.5 shadow-xs">
            <CalendarIcon />
            <span className="text-xs font-bold text-[#49454F]">Active Month:</span>
            <input
              type="month"
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(e.target.value)}
              className="border-0 focus:ring-0 text-sm font-bold text-[#6750A4] cursor-pointer outline-none"
            />
          </div>
        </div>

        {/* Operational Statistics Summaries of Current Period */}
        <section className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <PolishStatsCard
            title="Total Spent / مجموع المصروفات"
            value={`$${metrics.totalSpent.toFixed(2)}`}
            subtitle={`Invoices registered for ${selectedMonth}`}
            valueColor="text-[#B3261E]"
          />
          <PolishStatsCard
            title="Average Entry / معدل ميزانية المصروف"
            value={`$${metrics.averageExpense.toFixed(2)}`}
            subtitle="Per logged ticket"
            valueColor="text-[#6750A4]"
          />
          <PolishStatsCard
            title="Top Sink / الفئة الأكثر صرفاً"
            value={metrics.highestCategory.split(' ')[0]}
            subtitle={`Peak weight: $${metrics.highestAmount.toFixed(2)}`}
            valueColor="text-[#F57C00]"
          />
          <PolishStatsCard
            title="Authorized Signatory"
            value={isAdmin ? 'Ibrahim (ADMIN)' : 'READ ONLY'}
            subtitle={isAdmin ? 'Operational override enabled' : 'Insufficient administrative role'}
            valueColor={isAdmin ? 'text-[#2E7D32]' : 'text-[#757575]'}
          />
        </section>

        {activeTab === 'log' ? (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Form for inputting categories and dates */}
            <section className="lg:col-span-1">
              <PolishCard className="bg-white border-[#CAC4D0] sticky top-6">
                <div className="flex items-center justify-between mb-4 border-b pb-2">
                  <h3 className="text-base font-black text-[#6750A4]">
                    💳 Disburse Capital / قيد مصروف جديد
                  </h3>
                  <span className="px-2 py-0.5 text-3xs font-black rounded bg-red-100 text-[#B3261E] uppercase">
                    Admin Duty
                  </span>
                </div>

                <form onSubmit={handleAddExpense} className="space-y-4">
                  {formError && (
                    <div className="p-3 bg-red-50 border border-red-200 text-red-700 text-xs rounded-lg font-medium">
                      {formError}
                    </div>
                  )}

                  {/* Cash amount field */}
                  <PolishTextField
                    label="Disbursed Draw Value (مبلغ الصرف الفعلي $)*"
                    type="number"
                    step="0.01"
                    placeholder="e.g. 1450.00"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    required
                    disabled={!isAdmin}
                    helperText="Input currency in US Dollars."
                  />

                  {/* Expense categories dropdown */}
                  <div className="flex flex-col w-full mb-4">
                    <label className="text-xs font-bold mb-1.5 text-[#49454F]">
                      Allocation Category (نوع أو تصنيف المصروف ومحله)*
                    </label>
                    <select
                      value={category}
                      onChange={(e) => setCategory(e.target.value)}
                      disabled={!isAdmin}
                      className="block w-full rounded-lg border border-[#CAC4D0] px-3.5 py-3 text-sm text-[#1D1B20] bg-white transition-all focus:outline-none focus:ring-2 focus:ring-[#6750A4]/20 focus:border-[#6750A4]"
                      style={{ minHeight: '48px' }}
                    >
                      {categories.map((cat, idx) => (
                        <option key={idx} value={cat}>{cat}</option>
                      ))}
                    </select>
                  </div>

                  {/* Date Picker Input */}
                  <PolishTextField
                    label="Transaction Date (تاريخ القيد والفوترة)*"
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    required
                    disabled={!isAdmin}
                    helperText="Specify the actual day of purchasing action."
                  />

                  {/* Description note input */}
                  <PolishTextField
                    label="Detailed Reference / Description (البيان والتفاصيل)*"
                    type="text"
                    placeholder="e.g. Soldering flux, heating guns, shop clean supplies"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    required
                    disabled={!isAdmin}
                    helperText="Notes on the vendor, items purchased, or invoice ID."
                  />

                  <div className="pt-2">
                    <PolishButton
                      type="submit"
                      variant="danger"
                      className="w-full"
                      disabled={!isAdmin}
                      icon={PlusIcon}
                    >
                      Authorize Disbursal Cash
                    </PolishButton>
                  </div>

                  {!isAdmin && (
                    <p className="text-3xs text-center text-[#B3261E] mt-2 font-bold bg-amber-50 rounded p-2 border border-amber-200 leading-normal">
                      ⚠️ Role level read-only! Switch Access Role to "ADMIN" above to write data points. Just for simulation.
                    </p>
                  )}
                </form>
              </PolishCard>
            </section>

            {/* Expenses Records Table and Logs List */}
            <section className="lg:col-span-2">
              <div className="bg-white rounded-2xl border border-[#CAC4D0] p-5">
                
                {/* Internal table controls */}
                <div className="flex flex-col md:flex-row justify-between items-stretch md:items-center gap-3 mb-4">
                  <div>
                    <h3 className="text-base font-black text-[#1D1B20]">
                      📊 Disbursal Register / السجل العام للمصاريف
                    </h3>
                    <p className="text-3xs text-[#757575]">
                      Displaying filtered disbursements on periodic scopes
                    </p>
                  </div>

                  <div className="flex flex-wrap gap-2">
                    {/* Category Filter dropdown */}
                    <select
                      value={filterCategory}
                      onChange={(e) => setFilterCategory(e.target.value)}
                      className="bg-[#F3EDF7] hover:bg-[#EADDFF] text-xs font-bold text-[#49454F] border border-[#CAC4D0] px-3 py-1.5 rounded-lg outline-none"
                    >
                      <option value="All">All Categories</option>
                      {categories.map((c, idx) => (
                        <option key={idx} value={c}>{c.split(' ')[0]}</option>
                      ))}
                    </select>

                    <button
                      onClick={triggerDummyExport}
                      className="bg-white hover:bg-gray-50 border border-[#CAC4D0] p-1.5 rounded-lg text-[#49454F] flex items-center justify-center"
                      title="Export CSV"
                    >
                      <DownloadIcon />
                    </button>
                  </div>
                </div>

                {/* Live Sandbox Quick Search Input */}
                <div className="mb-4">
                  <PolishTextField
                    placeholder="Search logs by keyword..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="!mb-0"
                  />
                </div>

                {/* List Table wrapper responsive */}
                <PolishTable
                  headers={['Date / تاريخ', 'Allocation / فئة', 'Description / بيان', 'Logged By', 'Amount / مقدار']}
                  weights={[15, 20, 35, 15, 15]}
                  rows={filteredAndSearchedExpenses.map(exp => ({
                    date: (
                      <span className="text-xs font-mono text-[#49454F]">{exp.date}</span>
                    ),
                    category: (
                      <span className="text-3xs font-black uppercase text-[#6750A4] bg-[#EADDFF] px-2 py-0.5 rounded">
                        {exp.category.split(' ')[0]}
                      </span>
                    ),
                    description: (
                      <div>
                        <p className="text-xs font-bold text-[#1D1B20] max-w-xs overflow-hidden text-ellipsis whitespace-nowrap" title={exp.description}>
                          {exp.description}
                        </p>
                      </div>
                    ),
                    loggedBy: (
                      <span className="text-3xs text-gray-500 font-semibold">{exp.loggedBy}</span>
                    ),
                    amount: (
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-black text-[#B3261E]">
                          -${exp.amount.toFixed(2)}
                        </span>
                        {isAdmin && (
                          <button
                            onClick={() => handleDeleteExpense(exp.id)}
                            className="p-1 rounded-full hover:bg-red-50 ml-2 transition-colors duration-150"
                            title="Void transactional item"
                          >
                            <TrashIcon />
                          </button>
                        )}
                      </div>
                    )
                  }))}
                />

                <div className="mt-4 flex justify-between items-center text-3xs text-[#757575] font-semibold">
                  <p>
                    Showing {filteredAndSearchedExpenses.length} of {expenses.length} historic register entries.
                  </p>
                  <p className="text-right">
                    Total Listed Scope Outgo: <span className="text-[#B3261E] font-black">${filteredAndSearchedExpenses.reduce((s,i) => s + i.amount, 0).toFixed(2)}</span>
                  </p>
                </div>

              </div>
            </section>

          </div>
        ) : (
          /* Data visualization section showing business spending trends */
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Visual trends graph card */}
            <section className="lg:col-span-2">
              <PolishCard className="bg-white border-[#CAC4D0]">
                <div className="flex items-center justify-between mb-4 pb-2 border-b">
                  <div className="flex items-center space-x-2">
                    <TrendingUpIcon />
                    <h3 className="text-base font-black text-[#1D1B20]">
                      📈 Dynamic Expense Trends Area Plot (مخطط اتجاه الاستهلاك اليومي)
                    </h3>
                  </div>
                  <span className="px-2 py-0.5 text-3xs bg-[#EADDFF] text-[#21005D] rounded font-bold">
                    Daily Resolution
                  </span>
                </div>

                <p className="text-xs text-[#49454F] mb-6">
                  Following graph charts the daily operational disbursements in <b>{selectedMonth}</b>, highlighting spikes, purchases, and systemic cycles.
                </p>

                {/* Responsive SVG Chart showing spending trends */}
                <div className="w-full bg-[#FEF7FF] rounded-2xl border border-[#CAC4D0]/50 p-4">
                  <div className="relative h-64 w-full">
                    {/* SVG Canvas plotter */}
                    <svg className="w-full h-full overflow-visible" viewBox="0 0 500 200" preserveAspectRatio="none">
                      <defs>
                        <linearGradient id="chartGrad" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor="#B3261E" stopOpacity="0.25" />
                          <stop offset="100%" stopColor="#B3261E" stopOpacity="0.0" />
                        </linearGradient>
                      </defs>

                      {/* X and Y Grid lines */}
                      <line x1="0" y1="0" x2="500" y2="0" stroke="#CAC4D0" strokeWidth="0.5" strokeDasharray="4 4" />
                      <line x1="0" y1="50" x2="500" y2="50" stroke="#CAC4D0" strokeWidth="0.5" strokeDasharray="4 4" />
                      <line x1="0" y1="100" x2="500" y2="100" stroke="#CAC4D0" strokeWidth="0.5" strokeDasharray="4 4" />
                      <line x1="0" y1="150" x2="500" y2="150" stroke="#CAC4D0" strokeWidth="0.5" strokeDasharray="4 4" />
                      <line x1="0" y1="200" x2="500" y2="200" stroke="#CAC4D0" strokeWidth="1" />

                      {/* Line & Area Plot paths construction */}
                      {trendData.length > 1 && (() => {
                        const points = trendData.map((d, idx) => {
                          const x = (idx / (trendData.length - 1)) * 500;
                          const y = 200 - (d.amount / maxDailySpend) * 180; // keep padding room at the top
                          return `${x},${y}`;
                        });

                        const linePath = `M ${points.join(' L ')}`;
                        const areaPath = `${linePath} L 500,200 L 0,200 Z`;

                        return (
                          <>
                            {/* Area fill */}
                            <path d={areaPath} fill="url(#chartGrad)" />
                            {/* Primary Vector Line */}
                            <path d={linePath} fill="none" stroke="#B3261E" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
                            
                            {/* Interactive plotting points */}
                            {trendData.map((d, idx) => {
                              if (d.amount === 0) return null;
                              const x = (idx / (trendData.length - 1)) * 500;
                              const y = 200 - (d.amount / maxDailySpend) * 180;
                              return (
                                <g key={idx} className="group cursor-pointer">
                                  <circle
                                    cx={x}
                                    cy={y}
                                    r="4"
                                    fill="#B3261E"
                                    stroke="white"
                                    strokeWidth="1.5"
                                  />
                                  <title>{`Day ${idx + 1}: $${d.amount.toFixed(2)}`}</title>
                                </g>
                              );
                            })}
                          </>
                        );
                      })()}
                    </svg>

                    {/* Chart annotations */}
                    <div className="absolute top-2 left-2 bg-white/80 backdrop-blur-xs text-3xs border border-[#CAC4D0] rounded px-1.5 py-0.5 text-[#B3261E] font-bold">
                      Max Day Peak: ${maxDailySpend.toFixed(2)}
                    </div>
                  </div>

                  {/* Horizontal Timeline Indicators */}
                  <div className="flex justify-between mt-3 text-4xs font-bold text-[#757575] uppercase tracking-wider px-1">
                    <span>Day 01</span>
                    <span>Day 07</span>
                    <span>Day 15</span>
                    <span>Day 22</span>
                    <span>Day {trendData.length}</span>
                  </div>
                </div>

                <div className="mt-4 flex items-center p-3.5 bg-[#F3EDF7] rounded-xl border border-[#CAC4D0]/60">
                  <span className="text-xs mr-2 border-r pr-2 border-[#CAC4D0] text-[#B3261E] font-black">
                    💡 ANALYSIS REPORT
                  </span>
                  <p className="text-3xs text-[#49454F] font-bold">
                    Operational expenditure remains overall stable. {metrics.totalSpent > 3000 ? 'Peak capital outflows are occupied by facility rents and inventory stocking operations. Optimize stock thresholds in inventory tab to save cash velocity.' : 'Disbursements are within standard shop thresholds for small/medium business models.'}
                  </p>
                </div>
              </PolishCard>
            </section>

            {/* Categorization allocation panel */}
            <section className="lg:col-span-1">
              <PolishCard className="bg-white border-[#CAC4D0] h-full flex flex-col justify-between">
                <div>
                  <h3 className="text-base font-black text-[#1D1B20] mb-3 pb-2 border-b">
                    📦 Category Weight Allocation
                  </h3>
                  <p className="text-3xs text-[#757575] mb-4">
                    Visual weight of spending allocations in <b>{selectedMonth}</b>.
                  </p>
                  
                  {/* Category bars representing expenditures */}
                  <div className="space-y-4">
                    {categories.map((cat, idx) => {
                      const amountSpent = metrics.categoryBreakdown[cat] || 0;
                      const percentage = metrics.totalSpent > 0 ? (amountSpent / metrics.totalSpent) * 100 : 0;
                      
                      return (
                        <div key={idx} className="space-y-1">
                          <div className="flex justify-between items-center text-xs">
                            <span className="font-bold text-[#1D1B20]">{cat}</span>
                            <span className="text-3xs font-mono text-[#757575]">
                              ${amountSpent.toFixed(2)} ({percentage.toFixed(0)}%)
                            </span>
                          </div>
                          
                          <div className="w-full bg-[#CAC4D0]/30 rounded-full h-1.5 overflow-hidden">
                            <div
                              className="bg-[#B3261E] h-1.5 rounded-full"
                              style={{ width: `${percentage}%`, transition: 'width 0.4s ease-out' }}
                            />
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>

                <div className="mt-6 pt-4 border-t border-[#CAC4D0]/40 text-center">
                  <PolishButton
                    onClick={triggerDummyExport}
                    variant="outlined"
                    className="w-full"
                  >
                    Download Full Financial Summary
                  </PolishButton>
                </div>
              </PolishCard>
            </section>

          </div>
        )}

      </div>
    </div>
  );
}
