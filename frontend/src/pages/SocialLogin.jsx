// SocialLogin.jsx
import { FaFacebook, FaLinkedin, FaGoogle } from "react-icons/fa";

const SocialLogin = ({ isLogin, primaryColor }) => (
  <>
    <div className="relative mt-6">
      <div className="absolute inset-0 flex items-center">
        <div className="w-full border-t border-gray-300"></div>
      </div>
      <div className="relative flex justify-center text-sm">
        <span className="px-2 bg-white text-gray-500">
          {isLogin ? "Or login with" : "Or create account with"}
        </span>
      </div>
    </div>

    <div className="flex gap-4 mt-6 justify-center">
      <button
        type="button"
        className="w-12 h-12 flex items-center justify-center rounded-full border-2 border-gray-300 hover:border-purple-500 hover:bg-purple-50 transition"
      >
        <FaFacebook className="text-blue-600" size={20} />
      </button>
      <button
        type="button"
        className="w-12 h-12 flex items-center justify-center rounded-full border-2 border-gray-300 hover:border-purple-500 hover:bg-purple-50 transition"
      >
        <FaLinkedin className="text-blue-700" size={20} />
      </button>
      <button
        type="button"
        className="w-12 h-12 flex items-center justify-center rounded-full border-2 border-gray-300 hover:border-purple-500 hover:bg-purple-50 transition"
      >
        <FaGoogle className="text-red-600" size={20} />
      </button>
    </div>
  </>
);

export default SocialLogin;
