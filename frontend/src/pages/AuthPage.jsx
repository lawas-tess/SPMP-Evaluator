// AuthPage.jsx
import React, { useState, useEffect } from "react";
import AuthFormContainer from "./AuthFormContainer.jsx";

// Define slides here as they are static data for the UI
const slides = [
  "/overview.png", // Example of a dashboard screenshot
  "/upload.png", // Example of a successful evaluation
  "/documents.png", // Example of a key chart/graph
  "/task.png",
];

const AuthPage = () => {
  const [currentSlide, setCurrentSlide] = useState(0);

  // Slideshow interval
  useEffect(() => {
    const slideInterval = setInterval(() => {
      setCurrentSlide((prevIndex) => (prevIndex + 1) % slides.length);
    }, 5000);

    return () => clearInterval(slideInterval);
  }, [slides.length]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background-light to-background-dark flex items-center justify-center p-4">
      {/* Main container */}
      <div className="w-full max-w-7xl">
        {/* Layout: Left side (4/7) is wider than Right side (3/7) */}
        <div className="grid grid-cols-1 lg:grid-cols-7 gap-8 items-stretch">
          {/* Left side - Illustration and branding (WIDER COLUMN - STATIC) */}
          <div className="hidden lg:block lg:col-span-4" role="region">
            <div
              className={`w-full min-h-[40rem] bg-gradient-to-br from-primary-dark to-primary rounded-[40px] shadow-2xl flex flex-col justify-between p-10 relative overflow-hidden text-white`}
            >
              {/* Background Pattern Layer */}
              <div className="absolute inset-0 opacity-15 bg-[url('/path/to/subtle-grid.svg')] z-0 pointer-events-none"></div>

              {/* 1. BRANDING */}
              <div className="relative z-10 text-center ">
                <h1 className="text-3xl md:text-4xl font-extrabold tracking-wider">
                  QuickCheck
                </h1>
                <p className="text-xl font-medium opacity-95">
                  Evaluate SPMP Instantly
                </p>
              </div>

              {/* 2. SLIDESHOW DISPLAY AREA */}
              <div className="relative z-10 flex-grow flex items-center justify-center min-h-0 px-3">
                <img
                  key={currentSlide}
                  src={slides[currentSlide]}
                  className="max-h-full max-w-full object-contain rounded-xl shadow-2xl border-4 border-white/70 transition-opacity duration-700 ease-in-out"
                  alt={`SPMP Feature Screenshot ${currentSlide + 1}`}
                />
              </div>

              {/* 3. NAVIGATION DOTS */}
              <div className="relative z-10 pt-6 flex justify-center space-x-2">
                {slides.map((_, index) => (
                  <button
                    key={index}
                    onClick={() => setCurrentSlide(index)}
                    className={`h-2 transition-all duration-300 ${
                      index === currentSlide
                        ? "w-6 bg-white shadow-md rounded-full"
                        : "w-2 bg-white/50 hover:bg-white/80 rounded-full"
                    }`}
                    aria-label={`Go to slide ${index + 1}`}
                  />
                ))}
              </div>
            </div>
          </div>

          {/* Right side - Form Container (NARROWER COLUMN - DYNAMIC CONTENT) */}
          <div className="w-full lg:col-span-3 h-full">
            <AuthFormContainer />
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
