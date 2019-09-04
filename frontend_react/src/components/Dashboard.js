import React, {useState, useEffect} from 'react';
import Sensor from '../views/Sensor'

const chunk = (arr, size = 2) => {
  const newArr = []
  for (let i = 0; i < arr.length; i += size) {
    newArr.push(arr.slice(i, i + size))
  }
  return newArr
}

const BASE_URL = '0.0.0.0:8080';

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
    console.log(transform)
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

  return (
      <section>
        {
          chunk(sensors, 3).map( (slice, index) =>{
              return (
              <div className="tile is-parent" key={index}>
                {slice.map((sensor)=>{
                  return (<Sensor key={sensor.id} value={sensor} transformClicked={transformClicked}/>)
                })}
              </div>
              );
          })
        }
      </section>
  );
}

export default Dashboard;
