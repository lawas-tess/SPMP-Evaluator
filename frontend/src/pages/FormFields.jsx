// FormFields.jsx
import { AiOutlineEye, AiOutlineEyeInvisible } from "react-icons/ai";

export const TextInput = ({ label, name, value, onChange, placeholder }) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">
      {label}
    </label>
    <input
      type="text"
      name={name}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition pr-10"
      required
    />
  </div>
);

export const PasswordInput = ({ value, onChange, show, toggle }) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">
      Password
    </label>
    <div className="relative">
      <input
        type={show ? "text" : "password"}
        name="password"
        value={value}
        onChange={onChange}
        placeholder="8+ characters"
        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition pr-10"
        required
      />
      <button
        type="button"
        onClick={toggle}
        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
      >
        {show ? (
          <AiOutlineEyeInvisible size={20} />
        ) : (
          <AiOutlineEye size={20} />
        )}
      </button>
    </div>
  </div>
);

export const RoleSelect = ({ value, onChange }) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">Role</label>
    <select
      name="role"
      value={value}
      onChange={onChange}
      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition pr-10"
    >
      <option value="STUDENT">Student</option>
      <option value="PROFESSOR">Professor</option>
    </select>
  </div>
);
