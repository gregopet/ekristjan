/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
 	"./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
        fontFamily: {
            'logo': ['KgChasingPavements']
        },
        colors: {
            'background-green': '#265b2b',
        }
    },
  },
  plugins: [
	require("@tailwindcss/forms")
  ],
}
