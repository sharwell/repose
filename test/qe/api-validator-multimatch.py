#!/usr/bin/env python

import requests
import sys
import argparse

parser = argparse.ArgumentParser()
parser.add_argument(metavar='target-addr', dest='target_addr', help='Hostname or IP address of the target Repose node')
parser.add_argument(metavar='target-port', dest='target_port', help='Port of the target Repose node', type=int, default=8080, nargs='?')
parser.add_argument('protocol', help='Protocol to use to connect to the Repose node', choices=['http','https'], default='http', nargs='?')
parser.add_argument('--print-bad-response', help='Print out the response if it fails.', action='store_true')

args = parser.parse_args()

path = 'multimatch/sspnn'
roles_and_responses = {
  'role-0': 403,
  'role-1': 405,
  'role-2': 405,
  'role-3': 200,
  'role-4': 404,
  'role-5': 404 
  }

url = '%s://%s:%i/%s' % (args.protocol, args.target_addr, args.target_port, path)

correct = 0
incorrect = 0

for role, code in sorted(roles_and_responses.items()):
  print 'Getting "%s" with role "%s", expecting %i' % (url, role, code)
  resp = requests.get(url, headers = { 'X-Roles': role })
  if resp.status_code == code:
    correct += 1
    print 'CORRECT'
  else:
    incorrect += 1
    print 'INCORRECT: got %i instead' % resp.status_code
    if args.print_bad_response:
      print resp.content

print '%i correct' % correct
print '%i incorrect' % incorrect

