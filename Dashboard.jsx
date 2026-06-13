import React, { useState, useMemo } from 'react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts';
import {
  PolishButton,
  PolishTextField,
  PolishCard,
  PolishStatsCard,
  PolishTable
} from './UIComponents.jsx';

// Custom hook to monitor 'minLimit' in the Part database and determine parts requiring restocking
export function useRestockMonitor(partsList) {
  return useMemo(() => {
    const criticalParts = partsList.filter(p => p.quantity <= p.minLimit);
    return {
      hasCritical: criticalParts.length > 0,
      criticalParts,
      lowStockCount: criticalParts.length
    };
  }, [partsList]);
}

// Inline simple SVG icons to eliminate third-party dependency errors
const AddIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
  </svg>
);

const SearchIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
  </svg>
);

const TrashIcon = () => (
  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-4v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
  </svg>
);

export default function Dashboard() {
  const [role, setRole] = useState('admin'); // 'admin' | 'technician' | 'cashier'
  const [searchStr, setSearchStr] = useState('');
  const [showAddTicket, setShowAddTicket] = useState(false);

  // Form states
  const [ticketDevice, setTicketDevice] = useState('');
  const [ticketFault, setTicketFault] = useState('');
  const [ticketCustomer, setTicketCustomer] = useState('');

  // Sample Dashboard Data (Bilingual Mobile System State)
  const [repairTickets, setRepairTickets] = useState([
    { id: 1, ticketId: 'REC-3012', device: 'iPhone 15 Pro Max', fault: 'Broken OLED Screen / كسر شاشة داخلي', customerName: 'Ahmad Al-Harbi', status: 'received' },
    { id: 2, ticketId: 'CHK-9281', device: 'Samsung S24 Ultra', fault: 'No Overheating / Charge ICD issues / عطل شحن واهتزاز', customerName: 'Samar Ghamdi', status: 'checking' },
    { id: 3, ticketId: 'REC-4412', device: 'Google Pixel 8 Pro', fault: 'Rear Glass Shattered / كسر الزجاج الخلفي', customerName: 'Omar Shahrani', status: 'received' },
    { id: 4, ticketId: 'RDY-1092', device: 'iPad Air 5', fault: 'Battery Drain / استهلاك طاقة البطارية', customerName: 'Fatima Zahrani', status: 'completed' },
  ]);

  const [parts, setParts] = useState([
    { id: 1, name: 'iPhone 15 Pro OLED Display', code: 'IP15P-DISP', quantity: 12, minLimit: 5 },
    { id: 2, name: 'Samsung S24 Charging Port Board', code: 'S24-CHG-PRT', quantity: 2, minLimit: 4 },
    { id: 3, name: 'U2 Tristar Charging IC chip', code: 'IC-TRISTAR-3', quantity: 15, minLimit: 10 },
    { id: 4, name: 'iPad Polycarbonate Armor Case', code: 'IPD-CASE', quantity: 1, minLimit: 3 },
  ]);

  const [selectedStreamFilter, setSelectedStreamFilter] = useState('All Streams');
  const [revenueMonthlyTrend] = useState([
    { month: 'Jan 2026', cash: 2450, credit: 1200, installment: 950 },
    { month: 'Feb 2026', cash: 3100, credit: 1800, installment: 1100 },
    { month: 'Mar 2026', cash: 2800, credit: 2100, installment: 1300 },
    { month: 'Apr 2026', cash: 4100, credit: 1500, installment: 1600 },
    { month: 'May 2026', cash: 4800, credit: 3200, installment: 2400 },
    { month: 'Jun 2026', cash: 3820, credit: 2900, installment: 1800 }
  ]);

  // Derived metrics / Monitor with custom hook
  const { lowStockCount, criticalParts } = useRestockMonitor(parts);
  const pendingRepairsCount = repairTickets.filter(t => t.status !== 'completed').length;

  // Actions
  const handleAddTicket = (e) => {
    e.preventDefault();
    if (!ticketDevice || !ticketFault || !ticketCustomer) return;
    
    const newId = repairTickets.length + 1;
    const ticketId = `REC-${Math.floor(1000 + Math.random() * 9000)}`;
    
    setRepairTickets([
      ...repairTickets,
      {
        id: newId,
        ticketId,
        device: ticketDevice,
        fault: ticketFault,
        customerName: ticketCustomer,
        status: 'received'
      }
    ]);

    // Reset Form
    setTicketDevice('');
    setTicketFault('');
    setTicketCustomer('');
    setShowAddTicket(false);
  };

  const handleMoveTicket = (id, currentStatus) => {
    const nextStatus = currentStatus === 'received' ? 'checking' : 'completed';
    setRepairTickets(repairTickets.map(t => 
      t.id === id ? { ...t, status: nextStatus } : t
    ));
  };

  const handleDeleteTicket = (id) => {
    setRepairTickets(repairTickets.filter(t => t.id !== id));
  };

  // Filter tickets based on search query
  const filteredTickets = repairTickets.filter(t => 
    t.device.toLowerCase().includes(searchStr.toLowerCase()) ||
    t.ticketId.toLowerCase().includes(searchStr.toLowerCase()) ||
    t.customerName.toLowerCase().includes(searchStr.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-[#FEF7FF] text-[#1D1B20] p-6 font-sans">
      <div className="max-w-7xl mx-auto">
        
        {/* Header Ribbon & Role Switcher */}
        <header className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 pb-4 border-b border-[#CAC4D0]">
          <div>
            <h1 className="text-3xl font-black text-[#6750A4] tracking-tight">
              🔧 Refined Dashboard / لوحة التحكم الذكية
            </h1>
            <p className="text-sm text-[#49454F] mt-1">
              Cell Phone Repair & Shop System • Professional Polish Edition
            </p>
          </div>
          
          <div className="flex items-center space-x-2 mt-4 md:mt-0 bg-[#F3EDF7] p-1.5 rounded-full border border-[#CAC4D0]">
            <span className="text-xs font-bold text-[#49454F] px-3">Role:</span>
            {['admin', 'technician', 'cashier'].map(r => (
              <button
                key={r}
                onClick={() => setRole(r)}
                className={`px-4 py-1.5 text-xs font-bold rounded-full transition-all ${
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

        {/* 1. Upper Metrics Row */}
        <section className="grid grid-cols-1 md:grid-cols-4 gap-5 mb-8">
          <PolishStatsCard
            title="Total Catalog Parts (أصناف القطع)"
            value={parts.length.toString()}
            subtitle="Active components cataloged"
            valueColor="text-[#6750A4]"
          />
          <PolishStatsCard
            title="Low Stock Alert (نقص بالقطع)"
            value={lowStockCount.toString()}
            subtitle="Items below safety levels"
            valueColor={lowStockCount > 0 ? "text-[#B3261E]" : "text-[#2E7D32]"}
            containerColor={lowStockCount > 0 ? "bg-[#FFF9E6]" : "bg-[#E8F5E9]"}
            borderColor={lowStockCount > 0 ? "border-[#FFE082]" : "border-[#C8E6C9]"}
          />
          <PolishStatsCard
            title="Pending Repairs (قيد التصليح)"
            value={pendingRepairsCount.toString()}
            subtitle="Active service tickets"
            valueColor="text-[#F57C00]"
          />
          <PolishStatsCard
            title="Completed Tickets (الأعمال المنجزة)"
            value={(repairTickets.length - pendingRepairsCount).toString()}
            subtitle="Ready to pick up"
            valueColor="text-[#2E7D32]"
          />
        </section>

        {/* Low Stock Alert List / Notification Banner */}
        {lowStockCount > 0 && (
          <div className="mb-8 p-5 bg-[#FFEAEA] border border-[#FFCCD0] rounded-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
            <div className="flex items-start space-x-3.5">
              <span className="p-2 bg-[#B3261E] text-white rounded-xl animate-bounce">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
              </span>
              <div>
                <h4 className="font-black text-[#B3261E] text-sm uppercase tracking-wider flex items-center">
                  🚨 RESTOCK REQUIRED / تنبيه نقص مخزون قطع الصيانة
                </h4>
                <p className="text-xs text-[#601916] mt-0.5 font-medium">
                  The following spare hardware parts have dropped below their defined safety threshold level (`minLimit`):
                </p>
                <div className="flex flex-wrap gap-2 mt-3">
                  {criticalParts.map((p, idx) => (
                    <span key={idx} className="bg-white hover:bg-red-50 transition-colors border border-[#FFCCD0] text-3xs font-black uppercase tracking-tight text-[#B3261E] px-2.5 py-1 rounded-lg">
                      {p.name} ({p.quantity} left / limit {p.minLimit})
                    </span>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Interactive Revenue Trends Comparisons chart block */}
        <section className="bg-white rounded-2xl border border-[#CAC4D0] p-6 mb-8 shadow-xs">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center border-b pb-4 mb-4 gap-4">
            <div>
              <span className="text-3xs font-black uppercase text-[#6750A4] bg-[#EADDFF] px-2.5 py-1 rounded">
                Financial Revenue / مقارنة التدفقات النقدية
              </span>
              <h3 className="text-base font-black text-[#1D1B20] mt-1.5 flex items-center">
                📊 Sales Stream Channels Analysis (مقارنة المبيعات نقداً، آجل، وتقسيط)
              </h3>
              <p className="text-3xs text-[#757575] mt-0.5">
                Dynamic visual comparison of Cash, Credit, and Installment payment plans across modern fiscal quarters
              </p>
            </div>

            {/* Simulation of filters or quick toggles */}
            <div className="flex bg-[#F3EDF7] p-1 rounded-xl text-3xs font-bold gap-1 border border-[#CAC4D0]">
              {['All Streams', 'Cash Only', 'Credit Only', 'Installment Only'].map(stream => (
                <button
                  key={stream}
                  onClick={() => setSelectedStreamFilter(stream)}
                  className={`px-3 py-1.5 rounded-lg transition-all ${
                    selectedStreamFilter === stream ? 'bg-[#6750A4] text-white shadow-xs' : 'text-[#49454F] hover:bg-[#EADDFF]'
                  }`}
                  type="button"
                >
                  {stream}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
            
            {/* Recharts Bar Chart Visual Panel */}
            <div className="lg:col-span-3 bg-[#FEF7FF] rounded-2xl border border-[#CAC4D0]/40 p-4 transition-all duration-200">
              <div className="h-64 w-full relative">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={revenueMonthlyTrend}
                    margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" stroke="#CAC4D0" strokeOpacity={0.3} vertical={false} />
                    <XAxis 
                      dataKey="month" 
                      tick={{ fontSize: 9, fill: '#49454F', fontWeight: 'bold' }} 
                      axisLine={{ stroke: '#CAC4D0' }}
                      tickLine={false}
                    />
                    <YAxis 
                      tick={{ fontSize: 9, fill: '#49454F', fontWeight: 'mono' }} 
                      axisLine={false}
                      tickLine={false}
                    />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: '#FEF7FF', 
                        borderColor: '#CAC4D0', 
                        borderRadius: '12px',
                        boxShadow: '0 4px 12px rgba(103, 80, 164, 0.08)'
                      }} 
                      itemStyle={{ fontSize: '11px', fontWeight: 'bold' }}
                      labelStyle={{ fontSize: '11px', fontWeight: 'black', color: '#6750A4', marginBottom: '4px' }}
                    />
                    <Legend 
                      verticalAlign="top" 
                      height={36} 
                      iconType="circle" 
                      iconSize={8}
                      wrapperStyle={{ fontSize: '10px', fontWeight: 'bold' }} 
                    />
                    {(selectedStreamFilter === 'All Streams' || selectedStreamFilter === 'Cash Only') && (
                      <Bar dataKey="cash" name="Cash Sales (نقدي)" fill="#2E7D32" radius={[4, 4, 0, 0]} barSize={12} />
                    )}
                    {(selectedStreamFilter === 'All Streams' || selectedStreamFilter === 'Credit Only') && (
                      <Bar dataKey="credit" name="Credit Card (بطاقة)" fill="#6750A4" radius={[4, 4, 0, 0]} barSize={12} />
                    )}
                    {(selectedStreamFilter === 'All Streams' || selectedStreamFilter === 'Installment Only') && (
                      <Bar dataKey="installment" name="Installment Payment (تقسيط)" fill="#F57C00" radius={[4, 4, 0, 0]} barSize={12} />
                    )}
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>

            {/* Sidebar metrics for comparison inside the chart box */}
            <div className="lg:col-span-1 bg-[#F3EDF7] p-4 rounded-xl border border-[#CAC4D0] flex flex-col justify-between">
              <div>
                <h4 className="text-xs font-black text-[#6750A4] uppercase tracking-wider mb-3">
                  Channel Share / إجمالي الدخل
                </h4>
                
                <div className="space-y-3">
                  <div className="flex justify-between items-center pb-2 border-b border-gray-200">
                    <span className="flex items-center text-xs font-bold text-[#2E7D32]">
                      <span className="w-2.5 h-2.5 bg-[#2E7D32] rounded-full mr-1.5 inline-block"></span>
                      Cash Payments
                    </span>
                    <span className="text-xs font-black text-[#1D1B20] font-mono">
                      ${revenueMonthlyTrend.reduce((sum, d) => sum + d.cash, 0).toLocaleString()}
                    </span>
                  </div>

                  <div className="flex justify-between items-center pb-2 border-b border-gray-200">
                    <span className="flex items-center text-xs font-bold text-[#6750A4]">
                      <span className="w-2.5 h-2.5 bg-[#6750A4] rounded-full mr-1.5 inline-block"></span>
                      Credit Card
                    </span>
                    <span className="text-xs font-black text-[#1D1B20] font-mono">
                      ${revenueMonthlyTrend.reduce((sum, d) => sum + d.credit, 0).toLocaleString()}
                    </span>
                  </div>

                  <div className="flex justify-between items-center pb-2">
                    <span className="flex items-center text-xs font-bold text-[#F57C00]">
                      <span className="w-2.5 h-2.5 bg-[#F57C00] rounded-full mr-1.5 inline-block"></span>
                      Installments
                    </span>
                    <span className="text-xs font-black text-[#1D1B20] font-mono">
                      ${revenueMonthlyTrend.reduce((sum, d) => sum + d.installment, 0).toLocaleString()}
                    </span>
                  </div>
                </div>
              </div>

              <div className="pt-3.5 border-t border-gray-200 mt-2">
                <p className="text-4xs text-[#49454F] leading-normal font-bold">
                  💡 Analysis: Installments hold {((revenueMonthlyTrend.reduce((sum, d) => sum + d.installment, 0) / revenueMonthlyTrend.reduce((sum, d) => sum + (d.cash + d.credit + d.installment), 0)) * 100).toFixed(0)}% of sales weight, suggesting client financing yields significant device repair velocity.
                </p>
              </div>
            </div>

          </div>
        </section>

        {/* Search & Control Drawer Panel */}
        <div className="flex flex-col md:flex-row justify-between items-stretch md:items-center mb-6 gap-4">
          <div className="flex-1 max-w-md">
            <PolishTextField
              value={searchStr}
              onChange={(e) => setSearchStr(e.target.value)}
              placeholder="Search by ID, device or client..."
              label="Live Sandbox Finder (البحث في النظام)"
              icon={SearchIcon}
            />
          </div>

          {['admin', 'technician', 'cashier'].includes(role) && (
            <div className="flex items-center">
              <PolishButton
                onClick={() => setShowAddTicket(!showAddTicket)}
                variant="primary"
                icon={AddIcon}
              >
                New Repair Ticket / تذكرة جديدة
              </PolishButton>
            </div>
          )}
        </div>

        {/* Add Repair Window Overlay Component */}
        {showAddTicket && (
          <PolishCard className="mb-8 border-2 border-[#6750A4] bg-white max-w-2xl">
            <h3 className="text-lg font-black text-[#6750A4] mb-4 flex items-center">
              📝 Lodge New Bug / Repair Ticket (تسجيل جهاز للصيانة)
            </h3>
            <form onSubmit={handleAddTicket} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <PolishTextField
                  label="Device Name (اسم الجهاز)"
                  value={ticketDevice}
                  onChange={(e) => setTicketDevice(e.target.value)}
                  placeholder="e.g. iPhone 15 Pro Max"
                  required
                />
                <PolishTextField
                  label="Client Name (العميل)"
                  value={ticketCustomer}
                  onChange={(e) => setTicketCustomer(e.target.value)}
                  placeholder="e.g. Ahmad Al-Harbi"
                  required
                />
              </div>
              <PolishTextField
                label="Fault Description (العطل بالتفصيل)"
                value={ticketFault}
                onChange={(e) => setTicketFault(e.target.value)}
                placeholder="e.g. Cracked Front Glass, touchscreen unresponsive"
                required
              />
              <div className="flex justify-end space-x-3 pt-2">
                <PolishButton
                  variant="outlined"
                  onClick={() => setShowAddTicket(false)}
                >
                  Cancel
                </PolishButton>
                <PolishButton
                  type="submit"
                  variant="primary"
                >
                  Save Ticket
                </PolishButton>
              </div>
            </form>
          </PolishCard>
        )}

        {/* 2. Main Windows Board Layout */}
        <main className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          
          {/* Column 1: Received Kanban */}
          <section className="bg-white rounded-2xl border border-[#CAC4D0] p-5">
            <div className="flex justify-between items-center mb-4">
              <span className="px-3.5 py-1 text-xs font-black bg-[#EADDFF] text-[#21005D] rounded-full uppercase tracking-wider">
                📥 Received / مستلم ({filteredTickets.filter(t => t.status === 'received').length})
              </span>
            </div>
            
            <div className="space-y-4 overflow-y-auto max-h-[500px] pr-1">
              {filteredTickets.filter(t => t.status === 'received').length === 0 ? (
                <div className="text-center py-12 text-[#757575] text-sm">No devices in receiving panel.</div>
              ) : (
                filteredTickets.filter(t => t.status === 'received').map(t => (
                  <PolishCard key={t.id} containerColor="bg-[#FEF7FF]" className="border-[#CAC4D0]">
                    <div className="flex justify-between items-start">
                      <span className="text-xs font-black text-[#38BDF8]">{t.ticketId}</span>
                      {['admin', 'technician'].includes(role) && (
                        <button onClick={() => handleDeleteTicket(t.id)} className="text-[#B3261E] hover:bg-red-50 p-1.5 rounded-full">
                          <TrashIcon />
                        </button>
                      )}
                    </div>
                    <h4 className="font-bold text-sm text-[#1D1B20] mt-1.5">{t.device}</h4>
                    <p className="text-xs text-[#49454F] mt-1">{t.fault}</p>
                    <p className="text-2xs text-[#757575] mt-1.5 font-semibold">Client: {t.customerName}</p>
                    
                    {['admin', 'technician'].includes(role) && (
                      <div className="flex justify-end mt-4">
                        <PolishButton onClick={() => handleMoveTicket(t.id, t.status)} variant="secondary" className="!px-3 !py-1 text-2xs !min-h-[30px] !min-w-[auto]">
                          Investigate →
                        </PolishButton>
                      </div>
                    )}
                  </PolishCard>
                ))
              )}
            </div>
          </section>

          {/* Column 2: Checking Kanban */}
          <section className="bg-white rounded-2xl border border-[#CAC4D0] p-5">
            <div className="flex justify-between items-center mb-4">
              <span className="px-3.5 py-1 text-xs font-black bg-[#D1E4FF] text-[#001D35] rounded-full uppercase tracking-wider">
                🔍 Checking / فحص قيد العمل ({filteredTickets.filter(t => t.status === 'checking').length})
              </span>
            </div>
            
            <div className="space-y-4 overflow-y-auto max-h-[500px] pr-1">
              {filteredTickets.filter(t => t.status === 'checking').length === 0 ? (
                <div className="text-center py-12 text-[#757575] text-sm">No active tasks being checked.</div>
              ) : (
                filteredTickets.filter(t => t.status === 'checking').map(t => (
                  <PolishCard key={t.id} containerColor="bg-[#FEF7FF]" className="border-[#CAC4D0]">
                    <div className="flex justify-between items-start">
                      <span className="text-xs font-black text-[#FBBF24]">{t.ticketId}</span>
                      {['admin', 'technician'].includes(role) && (
                        <button onClick={() => handleDeleteTicket(t.id)} className="text-[#B3261E] hover:bg-red-50 p-1.5 rounded-full">
                          <TrashIcon />
                        </button>
                      )}
                    </div>
                    <h4 className="font-bold text-sm text-[#1D1B20] mt-1.5">{t.device}</h4>
                    <p className="text-xs text-[#49454F] mt-1">{t.fault}</p>
                    <p className="text-2xs text-[#757575] mt-1.5 font-semibold">Client: {t.customerName}</p>
                    
                    {['admin', 'technician'].includes(role) && (
                      <div className="flex justify-end mt-4">
                        <PolishButton onClick={() => handleMoveTicket(t.id, t.status)} variant="secondary" className="!px-3 !py-1 text-2xs !min-h-[30px] !min-w-[auto]">
                          Complete ✔
                        </PolishButton>
                      </div>
                    )}
                  </PolishCard>
                ))
              )}
            </div>
          </section>

          {/* Column 3: Ready Kanban */}
          <section className="bg-white rounded-2xl border border-[#CAC4D0] p-5">
            <div className="flex justify-between items-center mb-4">
              <span className="px-3.5 py-1 text-xs font-black bg-[#E8F5E9] text-[#2E7D32] rounded-full uppercase tracking-wider">
                ✅ Ready / جاهز للتسليم ({filteredTickets.filter(t => t.status === 'completed').length})
              </span>
            </div>
            
            <div className="space-y-4 overflow-y-auto max-h-[500px] pr-1">
              {filteredTickets.filter(t => t.status === 'completed').length === 0 ? (
                <div className="text-center py-12 text-[#757575] text-sm">No recently completed units.</div>
              ) : (
                filteredTickets.filter(t => t.status === 'completed').map(t => (
                  <PolishCard key={t.id} containerColor="bg-[#FEF7FF]" className="border-[#CAC4D0]">
                    <div className="flex justify-between items-start">
                      <span className="text-xs font-black text-[#34D399]">{t.ticketId}</span>
                      {['admin', 'technician'].includes(role) && (
                        <button onClick={() => handleDeleteTicket(t.id)} className="text-[#B3261E] hover:bg-red-50 p-1.5 rounded-full">
                          <TrashIcon />
                        </button>
                      )}
                    </div>
                    <h4 className="font-bold text-sm text-[#1D1B20] mt-1.5">{t.device}</h4>
                    <p className="text-xs text-green-700 font-bold bg-[#E8F5E9] p-1.5 rounded-lg border border-green-200 mt-2">
                      Ready for Collection (جاهز للتلقّي)
                    </p>
                    <p className="text-2xs text-[#757575] mt-1.5 font-semibold">Client: {t.customerName}</p>
                  </PolishCard>
                ))
              )}
            </div>
          </section>

        </main>

        {/* 3. Reusable Responsive Polish Table Grid */}
        <section className="mt-8 bg-white rounded-2xl border border-[#CAC4D0] p-6">
          <h3 className="text-lg font-black text-[#6750A4] mb-4">
            📊 Part Inventory Watchlist / مراقبة قطع الغيار والمخزون
          </h3>
          <PolishTable
            headers={['Part Name / الصنف', 'Item Reference / الكود', 'Stock Level / الكمية', 'Trigger Limit / الحد الأدنى', 'Status / الحالة']}
            weights={[30, 20, 15, 15, 20]}
            rows={parts.map(part => {
              const isLow = part.quantity <= part.minLimit;
              return {
                name: (
                  <span className="font-bold text-sm text-[#1D1B20]">{part.name}</span>
                ),
                code: (
                  <span className="bg-gray-100 border border-gray-200 text-[#49454F] text-xs font-mono px-2 py-0.5 rounded">
                    {part.code}
                  </span>
                ),
                quantity: (
                  <span className={`text-md font-black ${isLow ? 'text-[#B3261E]' : 'text-[#1D1B20]'}`}>
                    {part.quantity} units
                  </span>
                ),
                minLimit: (
                  <span className="text-xs text-[#757575]">{part.minLimit} units</span>
                ),
                status: (
                  <span className={`px-2.5 py-1 text-2xs font-bold rounded-lg ${
                    isLow 
                      ? 'bg-[#FFF9E6] text-[#B3261E] border border-[#FFE082]' 
                      : 'bg-[#E8F5E9] text-[#2E7D32] border border-[#C8E6C9]'
                  }`}>
                    {isLow ? '🚨 RESTOCK REQUIRED' : '✅ SAFE LEVELS'}
                  </span>
                )
              };
            })}
          />
        </section>

      </div>
    </div>
  );
}
