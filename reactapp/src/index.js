import React from 'react';
import axios from 'axios';
import ReactDOM from 'react-dom';
import './index.css';
import AWS from 'aws-sdk';


class ComparatorInitiator extends React.Component {

  render() {
    return (
      <div>
        <p> Click the button below to begin the AWS Rekognize demo</p>
        <button onClick={()=> this.props.onClick()}>
          Click to Start
        </button>
      </div>
    )
  }

}

class SimilarityRow extends React.Component {

  render(){
    console.log("in render row");
    console.log(this.props);
    console.log(this.props.data);
    console.log(this.props.images);

    const imgA = this.props.images.find((image) => 
      image.key === this.props.data.keyA
    )

    const imgB = this.props.images.find((image) => 
      image.key === this.props.data.keyB
    )

    console.log(imgA);
    console.log(imgB);


    if(this.props.images === undefined || this.props.images.length == 0){
      return(
        <tr key={this.props.data.key}>
          <td width="40%">{this.props.data.keyA}</td>
          <td width="40%">{this.props.data.keyB}</td>
          <td width="20%">{this.props.data.score}</td>
        </tr>
      )
    } else {
      return(
        <tr key={this.props.data.key}>
          <td width="40%">
            <img src={imgA.src} title={this.props.data.keyA}/>
          </td>
          <td width="40%">
            <img src={imgB.src} title={this.props.data.keyB}/>
          </td>
          <td width="20%">{this.props.data.score}</td>
        </tr>
      )
    }
  }
}

class ResultsTableClass extends React.Component {
  render(){
    console.log("In resultTable");
    console.log(this.props);

    return (
      <table>
        <thead>
          <tr>
            <th width="40%">Image A</th>
            <th width="40%">Image B</th>
            <th width="20%">Score</th>
          </tr>
        </thead>
        <tbody>
          {this.props.results.map((result) => (
              <SimilarityRow
                data={result}
                images={this.props.images}
              />
          ))}
        </tbody>        
      </table>
    );
  }
}

function encode(data){
  const buffer = Buffer.from(data);
  let base64 = buffer.toString('base64');
  return base64;
}

class Page extends React.Component {

  constructor(props) {
    super(props);
    
    AWS.config.update({
      region: 'us-east-1',
      credentials: new AWS.CognitoIdentityCredentials({
        IdentityPoolId: 'us-east-1:29497a54-556b-465b-a6e9-597866303601',
      })
    });

    const s3 = new AWS.S3();

    this.state={
      results:[],
      images:[],
      client: s3,
    };
    this.getResults = this.getResults.bind(this);
    this.getImage = this.getImage.bind(this);
  }

  getResults(){

    console.log("this was clicked");

    return axios.get('https://tjighzbhi1.execute-api.us-east-1.amazonaws.com/default/test-lambda-function', {crossdomain: true})
      .then(({data}) => {

        let dataJSON = JSON.parse(data);

        console.log(dataJSON);

        //Get Images
        let newImages = this.state.images;

        dataJSON.images.map((imageData) =>{
          if(!newImages.some(containedImage => containedImage.key === imageData.key)){
            this.getImage(dataJSON.bucket, imageData.key)
            .then((faceImg) => {

              newImages.push({
                key: imageData.key,
                image: faceImg,
                src: 'data:image/png;base64,' + encode(faceImg.Body)
              });
            });
          }
        });

        console.log(newImages);

        // let newResults = this.state.results;
        let newResults = [];

        dataJSON.results.map((result) => {
          newResults.push({
            key: result.key.keyA + '_' + result.key.keyB,
            keyA: result.key.keyA,
            keyB: result.key.keyB,
            score: result.score,
          });
        });

        this.setState({
          results: newResults,
          images: newImages
        })

        console.log("here is the statey");
        console.log(this.state);

        console.log(data);
      })
      .catch((err) => {
        console.log(err);
      });
  }

  getImage(bucketName, fileName){

    return new Promise((res, rej) => {
      let s3 = this.state.client;

      s3.getObject({
          Bucket: bucketName,
          Key: fileName
        }, (err,imageData) =>{
          if(err){
            console.log(err);
          }else{
            res(imageData);
          }
        }
      );
    })

    

  }

  renderTable() {
    return (
      <ResultsTableClass
        results={this.state.results}
        images={this.state.images}
      />
    );
  }

  render() {
    return (
      <div className="page">
        <div className="comparatorSection">
          <h1>AWS Rekognition Lambda Demo</h1>
          <ComparatorInitiator 
            onClick={() => this.getResults()}
          />
        </div>
        <div></div>
        <div className="resultsSection">
          {/* <div className="summarySection">

          </div> */}
          <div className="detailsSection">
            {this.renderTable()}
          </div>
        </div>
      </div>
    );
  }

}

ReactDOM.render(
  <Page />,
  document.getElementById('root')
);