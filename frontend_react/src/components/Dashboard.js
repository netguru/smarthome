import React, {useState, useEffect} from 'react';
import Sensor from '../views/Sensor'

const chunk = (arr, size = 2) => {
  const newArr = []
  for (let i = 0; i < arr.length; i += size) {
    newArr.push(arr.slice(i, i + size))
  }
  return newArr
}

const Dashboard = () => {
  async function fetchData() {
    const result = await fetch('http://192.168.0.21:8080/get_sensors_all')
    const sensors = await result.json();
    for (let index = 0; index < sensors.length; index++) {
      const sensor = sensors[index];  
      for (let j = 0; j < sensor.transforms.length; j++) {
        var transform = sensor.transforms[j];
        const event = await fetch(`http://192.168.0.21:8080/get_events_for_transform/${transform.id}/1`)
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
    const socket = new WebSocket('ws://192.168.0.21:8080/ws');
    socket.onopen = () => {
      socket.onmessage = (data) => {
        if(data === "REFRESH"){
          fetchData()
        }
      }
    }

    return () => {
      socket.close();
    }
  }, [])

  return (
      <section>
        {
          chunk(sensors, 3).map( (slice, index) =>{
              return (
              <div className="tile is-parent" key={index}>
                {slice.map((sensor)=>{
                  return (<Sensor key={sensor.id} value={sensor} />)
                })}
              </div>
              );
          })
        }
      </section>
  );
}

export default Dashboard;
