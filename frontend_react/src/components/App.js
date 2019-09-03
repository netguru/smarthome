import React , {Component} from 'react';
import {
  BrowserRouter as Router,
  Route,
  Link
} from 'react-router-dom';
import classNames from 'classnames'
import './App.scss';
import Dashboard from './Dashboard'
import Settings from './Settings'
import Sensors from './Sensors'

class App extends Component{
  state = {
    navMenuActive: false,
  }
  handleHamburgerClick = (e) => {
    e.preventDefault();
    this.setState({...this.state, navMenuActive: !this.state.navMenuActive});
  }

  render(){
  return (
    <div>
      <nav class="navbar is-fixed-top" role="navigation">
      <div class="navbar-brand">
        <div role="button" 
        className={classNames({"navbar-burger": true, "is-active": this.state.navMenuActive })}
           onClick={this.handleHamburgerClick}>
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
         </div>
      </div>
        <div className={classNames({"navbar-menu": true, "is-active": this.state.navMenuActive })} >
          <div class="navbar-start">
            <a class="navbar-item">
              Dashboard
            </a>
            <a class="navbar-item">
              Sensors
            </a>
          </div>

          <div class="navbar-end">
          <a class="navbar-item">
              Settings
            </a>
          </div>
        </div>
      </nav>
      <section>
        <Router>
          <div className="container">
            <Route exact path="/" component={Dashboard} />
            <Route path="/sensors" component={Sensors} />
            <Route path="/settings" component={Settings} />
          </div>
        </Router>
      </section>
    </div>
  );
  }
}

export default App;
