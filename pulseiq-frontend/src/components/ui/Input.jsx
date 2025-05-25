import React from 'react';
import { Eye, EyeOff } from 'lucide-react';

const Input = ({ icon: Icon, type = "text", placeholder, value, onChange, error, showPassword, onTogglePassword, ...props }) => (
  <div className="relative mb-4">
    <div className="relative">
      {Icon && <Icon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />}
      <input
        type={showPassword !== undefined ? (showPassword ? "text" : "password") : type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className={`w-full ${Icon ? 'pl-10' : 'pl-4'} pr-${type === 'password' ? '12' : '4'} py-3 border-2 rounded-xl transition-all duration-200 ${
          error ? 'border-red-300 bg-red-50' : 'border-gray-200 focus:border-blue-500 focus:bg-white'
        } outline-none`}
        {...props}
      />
      {type === 'password' && onTogglePassword && (
        <button
          type="button"
          onClick={onTogglePassword}
          className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
        >
          {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
        </button>
      )}
    </div>
    {error && <p className="text-red-500 text-sm mt-1 ml-1">{error}</p>}
  </div>
);

export default Input;