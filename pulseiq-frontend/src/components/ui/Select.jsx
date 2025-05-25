import React from 'react';

const Select = ({ icon: Icon, options, value, onChange, placeholder, error }) => (
  <div className="relative mb-4">
    <div className="relative">
      {Icon && <Icon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />}
      <select
        value={value}
        onChange={onChange}
        className={`w-full ${Icon ? 'pl-10' : 'pl-4'} pr-4 py-3 border-2 rounded-xl transition-all duration-200 ${
          error ? 'border-red-300 bg-red-50' : 'border-gray-200 focus:border-blue-500 focus:bg-white'
        } outline-none appearance-none bg-white`}
      >
        <option value="">{placeholder}</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
    {error && <p className="text-red-500 text-sm mt-1 ml-1">{error}</p>}
  </div>
);

export default Select;