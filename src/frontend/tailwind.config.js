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
            // https://coolors.co/3a4f41-edf7f6-f19953-001029
            'my-green': '#3a4f41', 'my-green-light': '#adc2b4',
            'azure': '#edf7f6',
            'sandy': '#f19953', 'sandy-light': '#fadec6',
            'my-blue-dark' : '#001029', 'my-blue': '#005ef5', 'my-blue-light': '#85b4ff',
        }
    },
  },
  plugins: [
	require("@tailwindcss/forms")
  ],
}
