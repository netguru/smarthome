import React, {useState, useEffect} from 'react';

const BASE_URL = '0.0.0.0:8080';

const Settings = () => {

  const [settings, setSettings] = useState({})
  const [viewState, setState] = useState({showSaved: false})

  async function fetchData() {
    const result = await fetch(`http://${BASE_URL}/settings`)
    const settings = await result.json();
    setSettings(settings);
  }

  useEffect(() => {
    fetchData();
  }, []);

  const saveSettingsClicked = async () => {
    const result = await fetch(`http://${BASE_URL}/save_settings`, {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(settings)
    })
    if(result.ok){
      setState({showSaved: true, value:"saved"})
    } else {
      setState({showSaved: true, value:"error"})
    }
    
    fetchData();
  }

  return (
      <section className="tile is-ancestor">

        <div className="tile is-vertical">

        
        <div className="tile is-horizontal is-12 is-parent">

          <div className="tile is-parent">
          <div className="tile is-child box">
  
              <h1 class="title">Db settings</h1>
              <div className="field">
                <label className="label">Url</label>
                <div className="control">
                  <input className="input" type="text" placeholder="Url" 
                  value={settings.dbUrl} 
                  onChange={(e)=> setSettings({...settings, dbUrl: e.target.value})} />
                </div>
              </div>

              <div className="field">
                <label className="label">User</label>
                <div className="control">
                  <input className="input" type="text" placeholder="User" value={settings.dbUser}
                  onChange={(e)=> setSettings({...settings, dbUser: e.target.value})}
                  />
                </div>
              </div>

              <div className="field">
                <label className="label">Password</label>
                <div className="control">
                  <input className="input" type="password" placeholder="Password" value={settings.dbPass}
                  onChange={(e)=> setSettings({...settings, dbPass: e.target.value})}
                  />
                </div>
              </div>
          </div>
          </div>

          <div className="tile is-parent">
          <div className="tile is-child box">


              <h1 class="title">Mqtt settings</h1>
              <div className="field">
                <label className="label">Url</label>
                <div className="control">
                  <input className="input" type="text" placeholder="Url" value={settings.mqttUrl}
                  onChange={(e)=> setSettings({...settings, mqttUrl: e.target.value})}
                  />
                </div>
              </div>

              <div className="field">
                <label className="label">User</label>
                <div className="control">
                  <input className="input" type="text" placeholder="User" value={settings.mqttUser}
                  onChange={(e)=> setSettings({...settings, mqttUser: e.target.value})}
                  />
                </div>
              </div>

              <div className="field">
                <label className="label">Password</label>
                <div className="control">
                  <input className="input" type="password" placeholder="Password" value={settings.mqttPass}
                  onChange={(e)=> setSettings({...settings, mqttPass: e.target.value})}
                  />
                </div>
              </div>
            </div>

        </div>
        </div>

        <div className="tile is-horizontal is-12 is-parent">
          <div className="tile is-parent">
            <div className="tile is-child is-2">
              <div className="button is-success" onClick={saveSettingsClicked}>Save settings</div>
            </div>
            <div className="tile is-child">
              {viewState.showSaved && <div> {viewState.value} </div> }
            </div>
          </div>
        </div>

        </div>
      </section>
  );
}

export default Settings;
