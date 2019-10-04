import React, {useState, useEffect} from 'react';
import Sensor from '../views/Sensor'
import EditSensor from '../views/EditSensor'


//TODO:
//1. error handling
const chunk = (arr, size = 2) => {
  const newArr = []
  for (let i = 0; i < arr.length; i += size) {
    newArr.push(arr.slice(i, i + size))
  }
  return newArr
}

const fabStyle = {
  bottom: "0px",
  position: "fixed",
  margin: "1em",
  right: "0px",
}

const BASE_URL = `${window.location.hostname}:${window.location.port}`;

const Dashboard = () => {
  
  async function fetchData() {
    const result = await fetch(`http://${BASE_URL}/get_sensors_all`)
    const sensors = await result.json();
    for (let index = 0; index < sensors.length; index++) {
      const sensor = sensors[index];  
      for (let j = 0; j < sensor.transforms.length; j++) {
        var transform = sensor.transforms[j];
        const event = await fetch(`http://${BASE_URL}/get_events_for_transform/${transform.id}/1`)
        const eventData = await event.json()
        sensor.transforms[j] = {...transform, event: eventData}
      }
    }
    // const sensors = [ 
    //   {name: "jeden", transforms: [{name:"tr1", id:1, returnType:"INT", event:[{data:10}]}]}, 
    //   {name: "jeden", transforms: [{name:"tr1", id:1, returnType:"INT", event:[{data:10}]},{name:"tr1", id:1, returnType:"INT", event:[{data:10}]}]}, 
    //   {name: "jeden", transforms: [{name:"tr1", id:1, returnType:"INT", event:[{data:10}]}]}, 
    //   {name: "jeden", transforms: [{name:"tr1", id:1, returnType:"INT", event:[{data:10}]}]}, 
    // ]
    setSensors(sensors);
  }

  const [sensors, setSensors] = useState([])


  useEffect(() => {
    fetchData();
  }, []);

  useEffect( () => {
    const socket = new WebSocket(`ws://${BASE_URL}/ws`);
    socket.onopen = () => {
      console.log("ws connected")
      socket.onmessage = (data) => {
        console.log(data)
        if(data.data === "REFRESH"){
          console.log("ws refresh")
          fetchData()
        }
      }
    }

    return () => {
      console.log("ws disconnected")
      socket.close();
    }
  }, [])

  const transformClicked = async (transform) => {
    if(transform.writable){
        let value = transform.event[0].data

        switch (transform.returnType) {
          case 'BOOLEAN':
            if (value.toLowerCase() === "true") {
              value = "false";
            } else {
              value = "true";
            }
            const event = {
              sensorId: transform.sensorId,
              transformId: transform.id,
              data: value,
            };
            const result = await fetch(`http://${BASE_URL}/add_event`, {
              method: 'PUT',
              headers: {'Content-Type': 'application/json'},
              body: JSON.stringify(event)
            })
            console.log(result)
            break;
          default:
            break;
        }
    }
  }

  const saveClicked = async (sensor) => {
    console.log(sensor)
    if(sensor.isNew){
      await fetch(`http://${BASE_URL}/add_sensor`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(sensor)
      })

    } else {
      await fetch(`http://${BASE_URL}/modify_sensor/${sensor.id}`, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(sensor)
      })
    }
    showEditDialog({isActive: false});
    fetchData();
  }

  const deleteClicked = async (sensor) => {
    console.log(sensor);
    await fetch(`http://${BASE_URL}/remove_sensor/${sensor.id}`, {
        method: 'DELETE',
        headers: {'Content-Type': 'application/json'},
      });
    showEditDialog({isActive: false});
    fetchData();
  }

  const [editSensorDialog, showEditDialog] = useState({isActive: false})

  return (
     <section>
      <section>
      {
          chunk(sensors, 3).map( (slice, index) =>{
              return (
              <div className="tile is-parent" key={index}>
                {slice.map((sensor)=>{
                  return (<Sensor key={sensor.id} value={sensor} transformClicked={transformClicked} editClicked={()=> showEditDialog({isActive: true, sensor: sensor})}/>)
                })}
              </div>
              );
          })
        }
      </section>
     
        <div className="button is-success" style={fabStyle} onClick={() => showEditDialog({isActive: true, sensor: null})}>
          <span className="icon">
            <i className="material-icons">add</i>
          </span>
        </div>

        <EditSensor isActive={editSensorDialog.isActive} 
                    sensor={editSensorDialog.sensor}
                    closeDialog={()=>showEditDialog({isActive: false})}
                    saveClicked={ saveClicked }
                    deleteClicked={deleteClicked}
                    />
      </section>
  );
}

export default Dashboard;


