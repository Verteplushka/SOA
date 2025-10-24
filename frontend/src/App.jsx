import React from 'react'
import CityList from './components/CityList'
import CityForm from './components/CityForm'
import GenocideActions from './components/GenocideActions'
import './App.css'


function App() {
return (
<div className="app-container">
<header>
<h1>City Management — Frontend</h1>
<p>Интерфейс для работы с API городов (XML).</p>
</header>


<main>
<section className="left">
<CityForm />
<GenocideActions />
</section>
<section className="right">
<CityList />
</section>
</main>


<footer>
<small>Frontend for Lab — expects XML API as in specification.</small>
</footer>
</div>
)
}


export default App