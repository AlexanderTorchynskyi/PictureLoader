Feature: Assets data feature

  Background:
    When user authenticate with password "test"
    Then the response status code should be 200

  Scenario: User calls a rest endpoint to receive assets data page
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag          |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 2  | tag1    | 12109        | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | LEVV022CA23Y1001 |
      | 3  | tag2    | 12109        | 373X_6_REV1 | PFEIFFER     | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 4  | tag2    | 1210923      | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | V022 AB22 Y2071  |
    When user sends GET request to endpoint /v1/assets/ with content-type application/json and empty headers
    Then the response status code should be 200
    And the "data.assetDToes" list size should be 4
    And I should see "page" json response with the following keys and values
      | totalElements | 4 |
      | totalPages    | 1 |
      | number        | 0 |

  Scenario: User calls a rest endpoint to get a asset data by id
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag         |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314 |
    When user sends GET request to endpoint /v1/assets/1 with content-type application/json and empty headers
    Then the response status code should be 200
    And I should see root json response with the following keys and values
      | id                  | 1                     |
      | tagName             | tag1                  |
      | serialNumber        | 2132205               |
      | type                | 373X_6_REV1           |
      | manufacturer        | SAMSON AG             |
      | productNumber       | 3730-6-11010200010000 |
      | lastMaintenanceDate | 1535443954232         |
      | longTag             | V022 CA23 Y4314       |

  Scenario: User calls a rest endpoint to get a assets data that no exist
    Given populate the following assets data
      | id    | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag         |
      | 12345 | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314 |
    When user sends GET request to endpoint /v1/assets/12345 with content-type application/json and empty headers
    Then the response status code should be 404
    And the json response has "apiError" object
    And I should see "apiError" json response with the following keys and values
      | errorCode         | MHTN_ERROR_003                      |
      | errorMessage      | object ASSET not found by id: 12345 |
      | detailedErrors.id | 12345                               |

  Scenario: User calls a rest endpoint to delete a assets data
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag          |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 2  | tag1    | 12109        | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | LEVV022CA23Y1001 |
      | 3  | tag2    | 12109        | 373X_6_REV1 | PFEIFFER     | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 4  | tag2    | 1210923      | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | V022 AB22 Y2071  |
    When user sends DELETE request to endpoint /v1/assets/1 with content-type application/json and empty headers
    Then the response status code should be 204

  Scenario: User calls a rest endpoint to delete a assets data that not exist
    When user sends DELETE request to endpoint /v1/assets/123456 with content-type application/json and empty headers
    Then the response status code should be 404
    And I should see "apiError" json response with the following keys and values
      | errorCode         | MHTN_ERROR_003                       |
      | errorMessage      | object ASSET not found by id: 123456 |
      | detailedErrors.id | 123456                               |


  Scenario: User calls a rest endpoint to post an asset
    When user sends POST request to endpoint /v1/assets with content-type application/json and empty headers and body
      | tagName             | "tag1"                  |
      | serialNumber        | 2132205                 |
      | type                | "373X_6_REV1"           |
      | manufacturer        | "SAMSON AG"             |
      | productNumber       | "3730-6-11010200010000" |
      | lastMaintenanceDate | 1535443954232           |
      | longTag             | "V022 CA23 Y4314"       |
    Then the response status code should be 201


  Scenario: User calls a rest endpoint to post an asset
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag         |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314 |
    When user sends POST request to endpoint /v1/assets with content-type application/json and empty headers
    Then the response status code should be 500


  Scenario: User calls a rest endpoint to get all assets with filter '='
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag          |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 2  | tag1    | 12109        | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | LEVV022CA23Y1001 |
      | 3  | tag2    | 12109        | 373X_6_REV1 | PFEIFFER     | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 4  | tag2    | 1210923      | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | V022 AB22 Y2071  |
    When user sends GET request to endpoint /v1/assets?filter=id:3 with content-type application/json and empty headers
    Then the response status code should be 200
    And the "data.assetDToes" list size should be 1
    And I should see "page" json response with the following keys and values
      | totalElements | 1 |
      | totalPages    | 1 |
      | number        | 0 |

  Scenario: User calls a rest endpoint to get all assets with filter '>'
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag          |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 2  | tag1    | 12109        | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | LEVV022CA23Y1001 |
      | 3  | tag2    | 12109        | 373X_6_REV1 | PFEIFFER     | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 4  | tag2    | 1210923      | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | V022 AB22 Y2071  |
    When user sends GET request to endpoint /v1/assets?filter=id>3 with content-type application/json and empty headers
    Then the response status code should be 200
    And the "data.assetDToes" list size should be 2
    And I should see "page" json response with the following keys and values
      | totalElements | 2 |
      | totalPages    | 1 |
      | number        | 0 |

  Scenario: User calls a rest endpoint to get all assets with filter that is not exist the regular data is returned
    Given populate the following assets data
      | id | tagName | serialNumber | type        | manufacturer | productNumber         | lastMaintenanceDate | longTag          |
      | 1  | tag1    | 2132205      | 373X_6_REV1 | SAMSON AG    | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 2  | tag1    | 12109        | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | LEVV022CA23Y1001 |
      | 3  | tag2    | 12109        | 373X_6_REV1 | PFEIFFER     | 3730-6-11010200010000 | 1535443954232       | V022 CA23 Y4314  |
      | 4  | tag2    | 1210923      | 373X_6_REV1 | Samson       | 3730-6-11010200010000 | 1535443954232       | V022 AB22 Y2071  |
    When user sends GET request to endpoint /v1/assets?filter=56897  with content-type application/json and empty headers
    Then the response status code should be 200
    And the "data.assetDToes" list size should be 4
    And I should see "page" json response with the following keys and values
      | totalElements | 4 |
      | totalPages    | 1 |
      | number        | 0 |