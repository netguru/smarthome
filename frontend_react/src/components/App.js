import React, { Component } from 'react';
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

class App extends Component {
  state = {
    navMenuActive: false,
  }
  handleHamburgerClick = (e) => {
    e.preventDefault();
    this.setState({ ...this.state, navMenuActive: !this.state.navMenuActive });
  }

  handleLinkClick = (e) => {
    this.setState({ ...this.state, navMenuActive: false });
  }

  render() {
    return (
      <Router>
        <nav className="navbar is-fixed-top" role="navigation">
          <div className="navbar-brand">
            <div role="button"
              className={classNames({ "navbar-burger": true, "is-active": this.state.navMenuActive })}
              onClick={this.handleHamburgerClick}>
              <span aria-hidden="true"></span>
              <span aria-hidden="true"></span>
              <span aria-hidden="true"></span>
            </div>
          </div>
          <div className={classNames({ "navbar-menu": true, "is-active": this.state.navMenuActive })} >
            <div className="navbar-start">
              <Link to="/" className="navbar-item" onClick={this.handleLinkClick}>Dashboard</Link>
              <Link to="/sensors" className="navbar-item" onClick={this.handleLinkClick}>Sensors</Link>
            </div>

            <div className="navbar-end">
              <Link to="/settings" className="navbar-item" onClick={this.handleLinkClick}>Settings</Link>
            </div>
          </div>
        </nav>

        <section className="section">
          <div className="container">
          <Route exact path="/" component={Dashboard} />
          <Route path="/sensors" component={Sensors} />
          <Route path="/settings" component={Settings} />
          </div>
          
        </section>
      </Router>
    );
  }
}

export default App;
