import React , {Component} from 'react';
import './App.scss';
import classNames from 'classnames'

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
      
    </div>
  );
  }
}

export default App;
