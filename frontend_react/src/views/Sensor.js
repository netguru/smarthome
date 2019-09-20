/* eslint-disable jsx-a11y/alt-text */
import React from 'react';

const Sensor = (props) => {

  const getPath = (transform, event) => {
    let icons = ''
    let data = ''
    switch (transform.returnType) {
      case 'BOOLEAN':
        icons = 'booleanIcons'
        data = event.data
        break;
      case 'INT':
        icons = 'intIcons'
        data= Math.floor(event.data / 10) * 10;
        break;
      default:
        break;
    }
    return `${process.env.PUBLIC_URL}/icons/${icons}/${transform.icon}-${data}.png`
  }
  return (
    <div className="tile is-parent is-4">
      <div className="tile is-child box">
        <div className="level is-mobile">
          <div className="level-left">
          <div className="level-item">
              <p className="title">{props.value.name}</p>
            </div>
          </div>
          <div className="level-right">
            <span className="icon is-small has-text-grey-light" onClick={props.editClicked}>
              <i className="material-icons">build</i>
            </span>
          </div>
        </div>
      <h1 className="title"> </h1>
      {props.value.transforms.map((transform)=>{
        return (
        <div className="level is-mobile" key={transform.id} onClick={() => props.transformClicked(transform)}>
          <div className="level-left">
            <div className="level-item">
              <p className="subtitle">{transform.name}</p>
            </div>
            
          </div>
          <div className="level-right">
            <div className="level-item">
              { (transform.icon === null || transform.returnType==='FLOAT' || transform.returnType==='STRING') &&
                transform.event[0].data
              }
              { (transform.returnType==='BOOLEAN' || transform.returnType==='INT') &&
                <img src={getPath(transform, transform.event[0])} width="30px"/>
              }
            </div>
          </div>
        </div>);
      })}
    </div>
    </div>
      
  );
}

export default Sensor;