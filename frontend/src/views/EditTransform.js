import React, { useState } from 'react';

const REMOVE = {
    background: 'rgba(180, 0, 0, 0.2)',
}
const ADD = {
    background: 'rgba(1, 180, 1, 0.2)',
}
const UPDATE = {
    background: 'rgba(255, 166, 0, 0.2)',
}

const NONE = {
    background: 'transparent',
}

const TransformEdit = (props) => {

    const booleanIcons=  [
        "contact",
        "door",
        "fire",
        "frontdoor",
        "garagedoor",
        "heating",
        "light",
        "lock",
        "network",
        "poweroutlet",
        "presence",
        "receiver",
        "screen",
        "siren",
        "switch",
        "wallswitch",
        "washingmachine",
        "window",
      ];

      const intIcons= [
        "battery",
        "blinds",
        "cinemascreen",
        "cistern",
        "garagedoor",
        "heating",
        "humidity",
        "light",
        "qualityofservice",
        "rollershutter",
        "sewerage",
      ];

    const getPath = (transform) => {
        let icons = ''
        let data = ''
        switch (transform.returnType) {
          case 'BOOLEAN':
            icons = 'booleanIcons'
            data = 'true'
            break;
          case 'INT':
            icons = 'intIcons'
            data= '100'
            break;
          default:
            break;
        }
        return `${process.env.PUBLIC_URL}/icons/${icons}/${transform.icon}-${data}.png`
      }

    const editTransform = (newValue) => {
        console.log(newValue);
        props.onChange(newValue);
    }

    const [state, setState] = useState({ expanded: false })

    let transform = props.transform
    if(transform.returnType == null){
        transform.returnType = "BOOLEAN"
    }
    let bg = NONE;
    switch (transform.action) {
        case "ADD":
            bg = ADD;
            break;
        case "REMOVE":
            bg = REMOVE;
            break;
        case "UPDATE":
            bg = UPDATE;
            break;
        default:
            bg = NONE;
            break;
    }
    
    return (
        <div className="box" style={bg}>
            <div className="level is-mobile" >
                <div className="level-left">
                    <div className="level-item" onClick={() => setState({ ...state, expanded: !state.expanded })}>
                        <p className="subtitle">{transform.name}</p>
                    </div>
                </div>
                <div className="level-right">
                    {(transform.action == null || (transform.action != null && (transform.action === 'ADD' || transform.action === 'UPDATE'))) &&
                        <span className="icon is-small has-text-grey-light"  style={{ margin: "10px" }} onClick={() => 
                            editTransform({ ...transform, action: 'REMOVE' })}>
                            <i className="material-icons">delete</i>
                        </span>
                    }
                    {transform.action != null && (transform.action === 'REMOVE' || transform.action === 'UPDATE') &&
                        <span className="icon is-small has-text-grey-light"  style={{ margin: "10px" }} onClick={() => {
                            setState({ ...state, iconChooser: "NONE" });
                            props.onCancel();
                        }}>
                            <i className="material-icons">cancel</i>
                        </span>
                    }

                </div>
            </div>
            {(state.expanded && !(transform.action != null && transform.action === 'REMOVE')) &&
                <div className="control">
                    <div className="level is-mobile">
                        <input className="input" type="text" value={transform.name}
                            placeholder="Name"
                            onChange={(e) => editTransform({ ...transform, action: "UPDATE", name: e.target.value })} />
                    </div>
                    <div className="level is-mobile">
                        <input className="input" type="text" value={transform.topic}
                            placeholder="topic"
                            onChange={(e) => editTransform({ ...transform, action: "UPDATE", topic: e.target.value })} />
                    </div>
                    <div className="level is-mobile">
                        <input className="input" type="text" value={transform.transform}
                            placeholder="JSON transform"
                            onChange={(e) => editTransform({ ...transform, action: "UPDATE", transform: e.target.value })} />
                    </div>
                    <div className="level is-mobile">
                        <div className="select">
                            <select value={transform.returnType}
                                onChange={(e) => {
                                    editTransform({ ...transform, action: "UPDATE", returnType: e.target.value });
                                    setState({ ...state, iconChooser: "NONE" });
                                }}>
                                <option>BOOLEAN</option>
                                <option>INT</option>
                                <option>FLOAT</option>
                                <option>STRING</option>
                            </select>
                        </div>
                    </div>
                    {transform.returnType === "BOOLEAN" &&
                        <div className="level is-mobile">
                            <input className="input" type="text" value={transform.boolTrue}
                                onChange={(e) => editTransform({ ...transform, action: "UPDATE", boolTrue: e.target.value })}
                                placeholder="On state label"
                            />
                            <input className="input" type="text" value={transform.boolFalse}
                                onChange={(e) => editTransform({ ...transform, action: "UPDATE", boolFalse: e.target.value })}
                                placeholder="Off state label"
                            />
                        </div>
                    }
                    <div className="level is-mobile">
                        <label className="checkbox">
                            <input style={{ margin: "10px" }} type="checkbox" checked={transform.writable}
                                onChange={(e) => editTransform({ ...transform, action: "UPDATE", writable: e.target.checked })}
                            />
                            Writable</label>

                    </div>
                    {transform.writable &&
                        <input className="input" type="text" value={transform.cmdTopic}
                            onChange={(e) => editTransform({ ...transform, action: "UPDATE", cmdTopic: e.target.value })}
                            placeholder="Write command topic"
                        />
                    }
                    {(transform.returnType === "BOOLEAN" || transform.returnType === "INT") &&
                        <div className="level is-mobile">
                            <div className="level-item">Icon:
                                {transform.icon == null &&
                                    <span className="icon is-small" style={{ margin: "10px" }}
                                        onClick={() => setState({ ...state, iconChooser: transform.returnType })}>
                                        <i className="material-icons">add_photo_alternate</i>
                                    </span>
                                }
                                {transform.icon != null &&
                                    <img src={getPath(transform)} alt="icon" width="60px"
                                    onClick={() => setState({ ...state, iconChooser: transform.returnType })}
                                    />
                                }

                            </div>

                        </div>
                    }

                    {state.iconChooser === "BOOLEAN" &&
                        <div className="level is-mobile">
                            <div className="buttons">
                                {booleanIcons.map((icon)=>{
                                    return (
                                        <img src={`${process.env.PUBLIC_URL}/icons/booleanIcons/${icon}-true.png`} alt="icon" width="60px"
                                    onClick={() => {
                                        setState({ ...state, iconChooser: "NONE"});
                                        editTransform({ ...transform, action: "UPDATE", icon: icon })
                                    }}
                                    />
                                    )
                                })}
                            </div>
                        </div>
                    }

                    {state.iconChooser === "INT" &&
                        <div className="level is-mobile">
                            <div className="buttons">
                                {intIcons.map((icon)=>{
                                    return (
                                        <img src={`${process.env.PUBLIC_URL}/icons/intIcons/${icon}-100.png`} alt="icon" width="60px"
                                    onClick={() => {
                                        setState({ ...state, iconChooser: "NONE"});
                                        editTransform({ ...transform, action: "UPDATE", icon: icon })
                                    }}
                                    />
                                    )
                                })}
                            </div>
                        </div>
                    }

                </div>
            }
        </div>

    )
}

export default TransformEdit;