/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';

const accountTransfer = require('./lib/accountTransfer');

module.exports.AccountTransfer = accountTransfer;
module.exports.contracts = [accountTransfer];
