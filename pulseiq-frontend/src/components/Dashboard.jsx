import React from 'react';
import { User, Heart, Stethoscope, Settings, UserCheck, LogOut } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import Button from './ui/Button';
//
// const Dashboard = () => {
//   const { user, logout } = useAuth();
//
//   const getUserIcon = (userType) => {
//     switch (userType) {
//       case 'PATIENT': return Heart;
//       case 'DOCTOR': return Stethoscope;
//       case 'TECHNICIAN': return Settings;
//       case 'ADMIN': return UserCheck;
//       default: return User;
//     }
//   };
//
//   const UserIcon = getUserIcon(user.userType);
//
//   return (
//     <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
//       {/* Header */}
//       <header className="bg-white shadow-lg border-b border-gray-200">
//         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
//           <div className="flex justify-between items-center h-16">
//             <div className="flex items-center">
//               <div className="w-10 h-10 bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl flex items-center justify-center">
//                 <Heart className="w-6 h-6 text-white" />
//               </div>
//               <h1 className="ml-3 text-2xl font-bold text-gray-800">PulseIQ</h1>
//             </div>
//
//             <div className="flex items-center space-x-4">
//               <div className="flex items-center space-x-2">
//                 <UserIcon className="w-6 h-6 text-gray-600" />
//                 <span className="text-gray-700 font-medium">{user.firstName} {user.lastName}</span>
//                 <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full">
//                   {user.userType}
//                 </span>
//               </div>
//
//               <Button
//                 variant="outline"
//                 size="sm"
//                 onClick={logout}
//               >
//                 <LogOut className="w-4 h-4" />
//                 Logout
//               </Button>
//             </div>
//           </div>
//         </div>
//       </header>
//
//       {/* Main Content */}
//       <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
//         <div className="bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
//           <div className="text-center">
//             <div className="w-24 h-24 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl mx-auto mb-6 flex items-center justify-center">
//               <UserIcon className="w-12 h-12 text-white" />
//             </div>
//
//             <h2 className="text-3xl font-bold text-gray-800 mb-2">
//               Welcome back, {user.firstName}!
//             </h2>
//
//             <p className="text-gray-600 mb-8">
//               You are logged in as a {user.userType.toLowerCase()}
//             </p>
//
//             <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
//               <div className="bg-gradient-to-r from-blue-50 to-cyan-50 p-6 rounded-xl border border-blue-100">
//                 <h3 className="text-lg font-semibold text-gray-800 mb-2">User ID</h3>
//                 <p className="text-gray-600">{user.userId}</p>
//               </div>
//
//               <div className="bg-gradient-to-r from-purple-50 to-pink-50 p-6 rounded-xl border border-purple-100">
//                 <h3 className="text-lg font-semibold text-gray-800 mb-2">Email</h3>
//                 <p className="text-gray-600">{user.email}</p>
//               </div>
//
//               <div className="bg-gradient-to-r from-green-50 to-emerald-50 p-6 rounded-xl border border-green-100">
//                 <h3 className="text-lg font-semibold text-gray-800 mb-2">Account Type</h3>
//                 <p className="text-gray-600">{user.userType}</p>
//               </div>
//             </div>
//           </div>
//         </div>
//       </main>
//     </div>
//   );
// };


// Updated Dashboard Component showing all user details
const Dashboard = () => {
  const { user, logout } = useAuth();

  // Debug: Log user object to console
  console.log('Current user object:', user);

  const getUserIcon = (userType) => {
    switch (userType) {
      case 'PATIENT': return Heart;
      case 'DOCTOR': return Stethoscope;
      case 'TECHNICIAN': return Settings;
      case 'ADMIN': return UserCheck;
      default: return User;
    }
  };

  const UserIcon = getUserIcon(user.userType);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      {/* Header */}
      <header className="bg-white shadow-lg border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl flex items-center justify-center">
                <Heart className="w-6 h-6 text-white" />
              </div>
              <h1 className="ml-3 text-2xl font-bold text-gray-800">PulseIQ</h1>
            </div>

            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <UserIcon className="w-6 h-6 text-gray-600" />
                <span className="text-gray-700 font-medium">{user.firstName} {user.lastName}</span>
                <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full">
                  {user.userType}
                </span>
              </div>

              <Button
                variant="outline"
                size="sm"
                onClick={logout}
              >
                <LogOut className="w-4 h-4" />
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
          <div className="text-center">
            <div className="w-24 h-24 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl mx-auto mb-6 flex items-center justify-center">
              <UserIcon className="w-12 h-12 text-white" />
            </div>

            <h2 className="text-3xl font-bold text-gray-800 mb-2">
              Welcome back, {user.firstName}!
            </h2>

            <p className="text-gray-600 mb-8">
              You are logged in as a {user.userType.toLowerCase()}
            </p>

            {/* Enhanced user details grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div className="bg-gradient-to-r from-blue-50 to-cyan-50 p-6 rounded-xl border border-blue-100">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">User ID</h3>
                <p className="text-gray-600">{user.userId}</p>
              </div>

              <div className="bg-gradient-to-r from-green-50 to-emerald-50 p-6 rounded-xl border border-green-100">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">First Name</h3>
                <p className="text-gray-600">{user.firstName}</p>
              </div>

              <div className="bg-gradient-to-r from-yellow-50 to-orange-50 p-6 rounded-xl border border-yellow-100">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Last Name</h3>
                <p className="text-gray-600">{user.lastName}</p>
              </div>

              <div className="bg-gradient-to-r from-purple-50 to-pink-50 p-6 rounded-xl border border-purple-100">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Email</h3>
                <p className="text-gray-600">{user.email}</p>
              </div>

              <div className="bg-gradient-to-r from-indigo-50 to-blue-50 p-6 rounded-xl border border-indigo-100">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Account Type</h3>
                <p className="text-gray-600">{user.userType}</p>
              </div>

              <div className="bg-gradient-to-r from-rose-50 to-red-50 p-6 rounded-xl border border-rose-100">
                <h3 className="text-lg font-semibold text-gray-800 mb-2">Token Status</h3>
                <p className="text-gray-600">{user.phone}</p>
              </div>
            </div>

            {/* Debug section - Remove this in production */}
{/*             <div className="mt-8 p-4 bg-gray-50 rounded-xl"> */}
{/*               <h3 className="text-lg font-semibold text-gray-800 mb-2">Debug: All User Data</h3> */}
{/*               <pre className="text-left text-sm text-gray-600 overflow-auto"> */}
{/*                 {JSON.stringify(user, null, 2)} */}
{/*               </pre> */}
{/*             </div> */}
          </div>
        </div>
      </main>
    </div>
  );
};


export default Dashboard;