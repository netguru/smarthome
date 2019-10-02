import React, { useState, useEffect } from 'react';
import classNames from 'classnames'
import cloneDeep from 'clone-deep'
import LabelToInput from './LabelToInput';
import TransformEdit from './EditTransform';

const EditSensor = (props) => {
  const [sensor, editSensor] = useState({name: "Add sensor", transforms: [], isNew: true})

  useEffect(()=>{
    let init = {name: "Add sensor", transforms: [], isNew: true}
    if(props.sensor != null){
      init = cloneDeep(props.sensor)
    }
    editSensor(init)
  }, [props.sensor])

  return (
    <div className={classNames({ "modal": true, "is-active": props.isActive })}>
      <div className="modal-background"></div>
      <div className="modal-card">
        <header className="modal-card-head">
          <div className="modal-card-title">
              <LabelToInput name={sensor.name} valueChanged={(value)=> {editSensor({...sensor, name: value})}} /> 
          </div>
          <button className="delete" style={{ margin: "10px" }} aria-label="close" onClick={props.closeDialog}></button>
        </header>
        <section className="modal-card-body">
          {sensor && sensor.transforms.map((transform, i) => {
            return <TransformEdit key={i} transform={transform} 
                      onChange={(newValue) => {
                        let current = sensor.transforms[i]
                        let newTransfoms = sensor.transforms;
                        if(newValue.action==="REMOVE" && current.action === "ADD"){
                          newTransfoms.splice(i,1);
                        } else if(current.action === "ADD") {
                          newValue.action = "ADD";
                          newTransfoms[i] = newValue;
                        } else {
                          newTransfoms[i] = newValue;
                        }
                        editSensor({...sensor, transforms: newTransfoms})
                      }}
                        
                      onCancel={()=> {
                        let newTransfoms = sensor.transforms;
                        newTransfoms[i] = props.sensor.transforms[i];
                        editSensor({...sensor, transforms: newTransfoms})
                      }}
                      />
          })}
          <div className="box button is-info is-rounded" onClick={()=>{
            let newTransfoms = []
            if(sensor.transforms!=null){
              newTransfoms = sensor.transforms
            }
            newTransfoms.push({name: "New characteristic", action: "ADD"})
            editSensor({...sensor, transforms: newTransfoms})
          }}>
            <span className="icon"><i className="material-icons">add</i></span>
          </div>
        </section>
        <footer className="modal-card-foot">
          <button className="button is-success" onClick={()=> props.saveClicked(sensor)}>Save changes</button>
          <button className="button" onClick={props.closeDialog}>Cancel</button>
        </footer>
      </div>
    </div>
  );
}

export default EditSensor;