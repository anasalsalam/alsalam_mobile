import React from 'react';

/**
 * PolishButton: A custom, highly reusable Button component with dynamic variants, 
 * hover states, focus outlines, and built-in interactive feedback.
 */
export const PolishButton = ({
  children,
  onClick,
  variant = 'primary', // 'primary' | 'secondary' | 'danger' | 'outlined'
  type = 'button',
  disabled = false,
  className = '',
  icon: Icon,
  ...props
}) => {
  const baseStyles = 'inline-flex items-center justify-center px-5 py-2.5 text-sm font-semibold rounded-full min-h-[44px] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';
  
  const variants = {
    primary: 'bg-[#6750A4] text-white hover:bg-[#523d85] focus:ring-[#6750A4]',
    secondary: 'bg-[#F3EDF7] text-[#49454F] hover:bg-[#EADDFF] focus:ring-[#6750A4]',
    danger: 'bg-[#B3261E] text-white hover:bg-[#8c1d18] focus:ring-[#B3261E]',
    outlined: 'bg-transparent border border-[#6750A4] text-[#6750A4] hover:bg-[#F3EDF7] focus:ring-[#6750A4]'
  };

  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      className={`${baseStyles} ${variants[variant]} ${className}`}
      style={{ minWidth: '48px', minHeight: '48px' }} // Standard safe touch target
      {...props}
    >
      {Icon && <span className="mr-2 inline-flex align-middle"><Icon size={18} /></span>}
      {children}
    </button>
  );
};

/**
 * PolishTextField: A floating/labeled Outlined Input field with persistent label colors,
 * clean typography, focused states, and error styling.
 */
export const PolishTextField = ({
  label,
  value,
  onChange,
  type = 'text',
  placeholder,
  error = false,
  helperText,
  disabled = false,
  className = '',
  icon: Icon,
  ...props
}) => {
  return (
    <div className={`flex flex-col w-full mb-4 ${className}`}>
      {label && (
        <label className={`text-xs font-bold mb-1.5 transition-colors duration-200 ${
          error ? 'text-[#B3261E]' : 'text-[#49454F] focus-within:text-[#6750A4]'
        }`}>
          {label}
        </label>
      )}
      <div className="relative rounded-lg shadow-sm">
        {Icon && (
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
            <Icon className="text-[#6750A4]" size={18} />
          </div>
        )}
        <input
          type={type}
          disabled={disabled}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          className={`block w-full rounded-lg border px-3.5 py-3 text-sm text-[#1D1B20] bg-white transition-all duration-205 focus:outline-none focus:ring-2 ${
            Icon ? 'pl-10' : ''
          } ${
            error
              ? 'border-[#B3261E] focus:border-[#B3261E] focus:ring-[#B3261E]/20'
              : 'border-[#CAC4D0] focus:border-[#6750A4] focus:ring-[#6750A4]/20'
          } disabled:bg-gray-100 disabled:text-gray-400`}
          style={{ minHeight: '48px' }} // Touch-friendly height
          {...props}
        />
      </div>
      {helperText && (
        <p className={`mt-1.5 text-xs ${error ? 'text-[#B3261E]' : 'text-[#757575]'}`}>
          {helperText}
        </p>
      )}
    </div>
  );
};

/**
 * PolishCard: An elegant material card utilizing negative space, custom border radius,
 * and high visual contrast with soft border outlines.
 */
export const PolishCard = ({
  children,
  onClick,
  containerColor = 'bg-white',
  borderColor = 'border-[#CAC4D0]',
  className = '',
  ...props
}) => {
  const clickableStyle = onClick ? 'cursor-pointer hover:shadow-md transition-shadow duration-200' : '';
  
  return (
    <div
      onClick={onClick}
      className={`rounded-2xl border p-5 ${borderColor} ${containerColor} ${clickableStyle} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

/**
 * PolishStatsCard: A dynamic metrics card used for financial and business summaries.
 */
export const PolishStatsCard = ({
  title,
  value,
  subtitle,
  valueColor = 'text-[#2E7D32]', // Default to success green
  className = '',
  ...props
}) => {
  return (
    <PolishCard containerColor="bg-[#F3EDF7]" className={`flex flex-col justify-between ${className}`} {...props}>
      <div>
        <span className="text-xs font-bold text-[#49454F] uppercase tracking-wider">{title}</span>
        <h3 className={`text-2xl font-black mt-1 mb-2 ${valueColor}`}>{value}</h3>
      </div>
      <p className="text-2xs text-[#757575] mt-1">{subtitle}</p>
    </PolishCard>
  );
};

/**
 * PolishTable: A responsive data table displaying records cleanly with high visual hierarchy.
 */
export const PolishTable = ({
  headers = [],
  weights = [], // Percentage or flex weights
  rows = [], // Array of row objects or raw arrays
  onRowClick,
  className = '',
  ...props
}) => {
  return (
    <div className={`overflow-x-auto rounded-xl border border-[#CAC4D0] bg-white ${className}`} {...props}>
      <table className="min-w-full divide-y divide-[#CAC4D0] table-fixed">
        <thead className="bg-[#EADDFF]">
          <tr>
            {headers.map((header, index) => {
              const width = weights[index] ? `${weights[index]}%` : 'auto';
              return (
                <th
                  key={index}
                  scope="col"
                  style={{ width }}
                  className="px-4 py-3 text-left text-xs font-bold text-[#21005D] uppercase tracking-wider overflow-hidden text-ellipsis whitespace-nowrap"
                >
                  {header}
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-[#CAC4D0]/50">
          {rows.length === 0 ? (
            <tr>
              <td colSpan={headers.length} className="px-4 py-8 text-center text-sm text-[#757575]">
                No records found / لا توجد سجلات حاليا
              </td>
            </tr>
          ) : (
            rows.map((row, rowIndex) => (
              <tr
                key={rowIndex}
                onClick={() => onRowClick && onRowClick(row, rowIndex)}
                className={`transition-colors duration-150 ${onRowClick ? 'cursor-pointer hover:bg-[#F3EDF7]/30' : ''} ${
                  rowIndex % 2 === 1 ? 'bg-[#F3EDF7]/10' : 'bg-white'
                }`}
              >
                {Object.values(row).slice(0, headers.length).map((cell, cellIndex) => (
                  <td
                    key={cellIndex}
                    className="px-4 py-3 text-sm text-[#1D1B20] overflow-hidden text-ellipsis whitespace-nowrap"
                  >
                    {cell}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};
