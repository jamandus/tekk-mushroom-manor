### Tekk Mushroom Manor

Tekk Mushroom Manor is an experimental project aimed at practicing and expanding proficiency in Java and AWS cloud services. This project utilizes an array of AWS resources to create a mushroom-related ecosystem.

You will find the full project backlog here: https://trello.com/b/GhjLZMoY/tekk-fungi

#### Description

The Tekk Mushroom Manor serves as the central repository for mushroom data. It is designed to store and provide information about mushroom locations, the Serverless database support batch actions and paginated scans.

#### Components

- **MushroomLocation (DynamoDB Table)**: The core component of Tekk Mushroom Manor, this DynamoDB table stores and streams detailed information about mushroom locations, including additional attributes such as harvest date, and toxicity.

#### Usage

To deploy and utilize Tekk Mushroom Manor, follow these steps:

- TBD

#### Policies

- **MushroomLocationReadWritePolicy**: Provides read and write permissions for interacting with the MushroomLocation DynamoDB table.
- **MushroomLocationReadOnlyPolicy**: Provides read-only permissions for querying mushroom location data.

#### Outputs

- **MushroomLocationTableName**: The name of the DynamoDB table that stores mushroom location data.
- **HarvestLocationIndexName**: The name of the index used for querying mushroom locations based on position and harvest date.
- **MushroomLocationStream**: The stream that propagates events for insertions, updates, and deletions in the MushroomLocation table.
- **MushroomLocationReadWritePolicyName**: The name of the read/write policy for accessing MushroomLocation.
- **MushroomLocationReadOnlyPolicyName**: The name of the read-only policy for accessing MushroomLocation.

#### Acknowledgments

- Special thanks to https://mbaeumer.github.io/ and ChatGPT for their support and guidance during the development of this project.